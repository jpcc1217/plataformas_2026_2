from datetime import datetime
from pathlib import Path
from typing import Annotated

from fastapi import FastAPI, Form, Request
from fastapi.responses import HTMLResponse, RedirectResponse
from fastapi.templating import Jinja2Templates

from portal.datos import FUNCIONARIOS, TIPOS, USUARIOS, Almacen

COOKIE_SESION = "sesion_sirh"
RETARDO_CARGA_MS = 1500

plantillas = Jinja2Templates(directory=Path(__file__).parent / "plantillas")


def crear_app(ui: str = "v1", inestable: bool = False) -> FastAPI:
    app = FastAPI(title="SIRH", docs_url=None, redoc_url=None, openapi_url=None)
    almacen = Almacen()

    def render(request: Request, plantilla: str, status_code: int = 200, **contexto) -> HTMLResponse:
        contexto.update(ui=ui, autenticado=almacen.sesion_valida(request.cookies.get(COOKIE_SESION)))
        return plantillas.TemplateResponse(request, plantilla, contexto, status_code=status_code)

    def a_ingreso(request: Request) -> RedirectResponse:
        destino = "/login?expirada=1" if request.cookies.get(COOKIE_SESION) else "/login"
        return RedirectResponse(destino, status_code=303)

    def sin_sesion(request: Request) -> bool:
        return not almacen.sesion_valida(request.cookies.get(COOKIE_SESION))

    def formulario(request: Request, error: str | None = None, valores: dict | None = None) -> HTMLResponse:
        return render(
            request,
            "novedad_nueva.html",
            tipos=TIPOS,
            retardo_ms=RETARDO_CARGA_MS,
            error=error,
            valores=valores or {},
        )

    @app.get("/")
    def inicio(request: Request):
        if sin_sesion(request):
            return a_ingreso(request)
        return RedirectResponse("/novedades/nueva", status_code=303)

    @app.get("/login", response_class=HTMLResponse)
    def formulario_login(request: Request, expirada: int = 0):
        aviso = "Su sesión expiró. Ingrese nuevamente." if expirada else None
        respuesta = render(request, "login.html", error=aviso)
        respuesta.delete_cookie(COOKIE_SESION)
        return respuesta

    @app.post("/login", response_class=HTMLResponse)
    def procesar_login(
        request: Request,
        usuario: Annotated[str, Form()] = "",
        clave: Annotated[str, Form()] = "",
    ):
        if USUARIOS.get(usuario.strip()) != clave:
            return render(request, "login.html", error="Credenciales incorrectas")
        respuesta = RedirectResponse("/novedades/nueva", status_code=303)
        respuesta.set_cookie(COOKIE_SESION, almacen.abrir_sesion(), httponly=True, samesite="lax")
        return respuesta

    @app.get("/salir")
    def salir(request: Request):
        almacen.cerrar_sesion(request.cookies.get(COOKIE_SESION))
        respuesta = RedirectResponse("/login", status_code=303)
        respuesta.delete_cookie(COOKIE_SESION)
        return respuesta

    @app.get("/novedades/nueva", response_class=HTMLResponse)
    def nueva_novedad(request: Request):
        if sin_sesion(request):
            return a_ingreso(request)
        return formulario(request)

    @app.post("/novedades", response_class=HTMLResponse)
    def registrar_novedad(
        request: Request,
        solicitud: Annotated[str, Form()] = "",
        cedula: Annotated[str, Form()] = "",
        tipo: Annotated[str, Form()] = "",
        fecha: Annotated[str, Form()] = "",
        dias: Annotated[str, Form()] = "",
    ):
        token = request.cookies.get(COOKIE_SESION)
        if sin_sesion(request):
            return a_ingreso(request)
        if inestable and almacen.contar_envio() % 3 == 0:
            return render(request, "no_disponible.html", status_code=503)

        valores = {"solicitud": solicitud, "cedula": cedula, "tipo": tipo, "fecha": fecha, "dias": dias}
        cedula = cedula.strip()
        error = None
        fecha_inicio = None
        if not solicitud.strip():
            error = "El número de solicitud es obligatorio"
        elif cedula not in FUNCIONARIOS:
            error = f"La cédula {cedula or '(vacía)'} no corresponde a un funcionario activo"
        elif tipo not in TIPOS:
            error = "Seleccione un tipo de novedad válido"
        else:
            try:
                fecha_inicio = datetime.strptime(fecha.strip(), "%d/%m/%Y").date()
            except ValueError:
                error = "La fecha de inicio debe tener el formato dd/mm/aaaa"
        if not error and (not dias.strip().isdigit() or not 1 <= int(dias) <= 15):
            error = "Los días hábiles deben ser un número entero entre 1 y 15"
        if not error and tipo == "VAC" and int(dias) > almacen.saldo(cedula):
            error = f"Saldo insuficiente: el funcionario tiene {almacen.saldo(cedula)} días de vacaciones disponibles"
        if error:
            return formulario(request, error, valores)

        novedad = almacen.registrar(solicitud.strip(), cedula, tipo, fecha_inicio, int(dias))
        almacen.contar_registro_en_sesion(token)
        return render(request, "novedad_registrada.html", novedad=novedad)

    @app.get("/novedades", response_class=HTMLResponse)
    def consulta(request: Request, solicitud: str | None = None):
        if sin_sesion(request):
            return a_ingreso(request)
        return render(request, "novedades.html", novedades=almacen.buscar(solicitud), solicitud=solicitud or "")

    return app
