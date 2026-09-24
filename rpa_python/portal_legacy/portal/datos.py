import secrets
from dataclasses import dataclass
from datetime import datetime
from threading import Lock

USUARIOS = {"operador.rpa": "RpaFarmacia2026"}

CATALOGO = {
    "MED-001": "Acetaminofén 500 mg x 100 tabletas",
    "MED-002": "Ibuprofeno 400 mg x 50 tabletas",
    "MED-003": "Amoxicilina 500 mg x 21 cápsulas",
    "MED-004": "Loratadina 10 mg x 10 tabletas",
    "MED-005": "Omeprazol 20 mg x 30 cápsulas",
    "MED-006": "Metformina 850 mg x 30 tabletas",
}

PROVEEDORES = [
    "Distribuidora Andina",
    "Laboratorios del Valle",
    "Droguería Central Mayorista",
]


@dataclass(frozen=True)
class Pedido:
    radicado: str
    referencia: str
    codigo: str
    medicamento: str
    cantidad: int
    proveedor: str
    registrado_en: datetime


class Almacen:
    def __init__(self) -> None:
        self._pedidos: list[Pedido] = []
        self._sesiones: set[str] = set()
        self._envios = 0
        self._lock = Lock()

    def abrir_sesion(self) -> str:
        token = secrets.token_urlsafe(24)
        with self._lock:
            self._sesiones.add(token)
        return token

    def cerrar_sesion(self, token: str | None) -> None:
        with self._lock:
            self._sesiones.discard(token or "")

    def sesion_valida(self, token: str | None) -> bool:
        return bool(token) and token in self._sesiones

    def contar_envio(self) -> int:
        with self._lock:
            self._envios += 1
            return self._envios

    def registrar(self, referencia: str, codigo: str, cantidad: int, proveedor: str) -> Pedido:
        with self._lock:
            ahora = datetime.now()
            pedido = Pedido(
                radicado=f"PED-{ahora.year}-{len(self._pedidos) + 1:06d}",
                referencia=referencia,
                codigo=codigo,
                medicamento=CATALOGO[codigo],
                cantidad=cantidad,
                proveedor=proveedor,
                registrado_en=ahora,
            )
            self._pedidos.append(pedido)
            return pedido

    def buscar(self, referencia: str | None = None) -> list[Pedido]:
        if not referencia:
            return list(self._pedidos)
        objetivo = referencia.strip().upper()
        return [p for p in self._pedidos if p.referencia.upper() == objetivo]
