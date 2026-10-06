import secrets
from dataclasses import dataclass
from datetime import date, datetime
from threading import Lock

USUARIOS = {"operador.rpa": "NominaSirh2026"}

REGISTROS_POR_SESION = 3

FUNCIONARIOS = {
    "1017234567": "Laura Restrepo Gómez",
    "71654321": "Carlos Andrés Mejía Toro",
    "43987654": "Diana Patricia Ríos Londoño",
    "1036789012": "Santiago Ospina Vélez",
    "98765432": "Jorge Iván Cardona Arias",
    "1152345678": "Valentina Zapata Arango",
}

SALDOS_INICIALES = {
    "1017234567": 15,
    "71654321": 4,
    "43987654": 10,
    "1036789012": 8,
    "98765432": 20,
    "1152345678": 6,
}

TIPOS = {
    "VAC": "Vacaciones",
    "PRM": "Permiso remunerado",
    "LNR": "Licencia no remunerada",
}


@dataclass(frozen=True)
class Novedad:
    resolucion: str
    solicitud: str
    cedula: str
    funcionario: str
    tipo: str
    fecha_inicio: date
    dias: int
    registrada_en: datetime


class Almacen:
    def __init__(self) -> None:
        self._novedades: list[Novedad] = []
        self._sesiones: dict[str, int] = {}
        self._saldos = dict(SALDOS_INICIALES)
        self._envios = 0
        self._lock = Lock()

    def abrir_sesion(self) -> str:
        token = secrets.token_urlsafe(24)
        with self._lock:
            self._sesiones[token] = 0
        return token

    def cerrar_sesion(self, token: str | None) -> None:
        with self._lock:
            self._sesiones.pop(token or "", None)

    def sesion_valida(self, token: str | None) -> bool:
        return bool(token) and token in self._sesiones

    def contar_registro_en_sesion(self, token: str) -> None:
        with self._lock:
            self._sesiones[token] += 1
            if self._sesiones[token] >= REGISTROS_POR_SESION:
                del self._sesiones[token]

    def contar_envio(self) -> int:
        with self._lock:
            self._envios += 1
            return self._envios

    def saldo(self, cedula: str) -> int:
        return self._saldos[cedula]

    def registrar(self, solicitud: str, cedula: str, codigo_tipo: str, fecha_inicio: date, dias: int) -> Novedad:
        with self._lock:
            if codigo_tipo == "VAC":
                self._saldos[cedula] -= dias
            ahora = datetime.now()
            novedad = Novedad(
                resolucion=f"RH-{ahora.year}-{len(self._novedades) + 1:06d}",
                solicitud=solicitud,
                cedula=cedula,
                funcionario=FUNCIONARIOS[cedula],
                tipo=TIPOS[codigo_tipo],
                fecha_inicio=fecha_inicio,
                dias=dias,
                registrada_en=ahora,
            )
            self._novedades.append(novedad)
            return novedad

    def buscar(self, solicitud: str | None = None) -> list[Novedad]:
        if not solicitud:
            return list(self._novedades)
        objetivo = solicitud.strip().upper()
        return [n for n in self._novedades if n.solicitud.upper() == objetivo]
