from pathlib import Path
from typing import Annotated

from fastapi import FastAPI, Form, Request
from fastapi.responses import HTMLResponse, RedirectResponse
from fastapi.templating import Jinja2Templates

from portal.datos import CATALOGO, PROVEEDORES, USUARIOS, Almacen

COOKIE_SESION = "sesion_portal"
RETARDO_CARGA_MS = 1200

plantillas = Jinja2Templates(directory=Path(__file__).parent / "plantillas")


def crear_app(ui: str = "v1", inestable: bool = False) -> FastAPI:
    app = FastAPI(title="Portal legacy de pedidos", docs_url=None, redoc_url=None, openapi_url=None)
    almacen = Almacen()

    def render(request: Request, plantilla: str, status_code: int = 200, **contexto) -> HTMLResponse:
        contexto.update(ui=ui, autenticado=almacen.sesion_valida(request.cookies.get(COOKIE_SESION)))
        return plantillas.TemplateResponse(request, plantilla, contexto, status_code=status_code)

    def sin_sesion(request: Request) -> bool:
        return not almacen.sesion_valida(request.cookies.get(COOKIE_SESION))

    @app.get("/")
    def inicio(request: Request):
        destino = "/login" if sin_sesion(request) else "/pedidos/nuevo"
        return RedirectResponse(destino, status_code=303)

    @app.get("/login", response_class=HTMLResponse)
    def formulario_login(request: Request):
        return render(request, "login.html", error=None)

    @app.post("/login", response_class=HTMLResponse)
    def procesar_login(
        request: Request,
        usuario: Annotated[str, Form()] = "",
        clave: Annotated[str, Form()] = "",
    ):
        if USUARIOS.get(usuario.strip()) != clave:
            return render(request, "login.html", error="Usuario o contraseña inválidos")
        respuesta = RedirectResponse("/pedidos/nuevo", status_code=303)
        respuesta.set_cookie(COOKIE_SESION, almacen.abrir_sesion(), httponly=True, samesite="lax")
        return respuesta

    @app.get("/salir")
    def salir(request: Request):
        almacen.cerrar_sesion(request.cookies.get(COOKIE_SESION))
        respuesta = RedirectResponse("/login", status_code=303)
        respuesta.delete_cookie(COOKIE_SESION)
        return respuesta

    @app.get("/pedidos/nuevo", response_class=HTMLResponse)
    def formulario_pedido(request: Request):
        if sin_sesion(request):
            return RedirectResponse("/login", status_code=303)
        return render(
            request,
            "pedido_nuevo.html",
            proveedores=PROVEEDORES,
            retardo_ms=RETARDO_CARGA_MS,
            error=None,
            valores={},
        )

    @app.post("/pedidos", response_class=HTMLResponse)
    def registrar_pedido(
        request: Request,
        referencia: Annotated[str, Form()] = "",
        codigo: Annotated[str, Form()] = "",
        cantidad: Annotated[str, Form()] = "",
        proveedor: Annotated[str, Form()] = "",
    ):
        if sin_sesion(request):
            return RedirectResponse("/login", status_code=303)
        if inestable and almacen.contar_envio() % 3 == 0:
            return render(request, "no_disponible.html", status_code=503)

        valores = {"referencia": referencia, "codigo": codigo, "cantidad": cantidad, "proveedor": proveedor}
        codigo = codigo.strip().upper()
        error = None
        if not referencia.strip():
            error = "La referencia interna es obligatoria"
        elif codigo not in CATALOGO:
            error = f"El código {codigo or '(vacío)'} no existe en el catálogo"
        elif not cantidad.strip().isdigit() or int(cantidad) <= 0:
            error = "La cantidad debe ser un número entero mayor que cero"
        elif proveedor not in PROVEEDORES:
            error = "Seleccione un proveedor válido"
        if error:
            return render(
                request,
                "pedido_nuevo.html",
                proveedores=PROVEEDORES,
                retardo_ms=RETARDO_CARGA_MS,
                error=error,
                valores=valores,
            )

        pedido = almacen.registrar(referencia.strip(), codigo, int(cantidad), proveedor)
        return render(request, "pedido_registrado.html", pedido=pedido)

    @app.get("/pedidos", response_class=HTMLResponse)
    def listado(request: Request, referencia: str | None = None):
        if sin_sesion(request):
            return RedirectResponse("/login", status_code=303)
        return render(request, "pedidos.html", pedidos=almacen.buscar(referencia), referencia=referencia or "")

    return app
