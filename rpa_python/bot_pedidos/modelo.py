from dataclasses import dataclass


@dataclass(frozen=True)
class Pedido:
    referencia: str
    codigo: str
    cantidad: int
    proveedor: str