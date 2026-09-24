# Portal legacy de pedidos a proveedores

Sistema objetivo del bot RPA de la clase 2. Simula una aplicación web heredada sin API pública: el único modo de registrar pedidos es a través de sus formularios.

## Arranque

Requiere `uv` 0.8 o superior. Desde la carpeta `portal_legacy/`:

```bash
uv run python -m portal
```

El portal queda disponible en <http://127.0.0.1:8010>. Credenciales del operador: `operador.rpa` / `RpaFarmacia2026`.

Los datos viven en memoria: al detener el portal (`Ctrl+C`) se pierden todos los pedidos registrados.

## Opciones

| Opción | Efecto |
|---|---|
| `--ui v2` | Interfaz rediseñada: cambian los `id`, las clases y la estructura HTML; se conservan las etiquetas visibles. |
| `--inestable` | Uno de cada tres envíos de pedido responde `503 Servicio temporalmente no disponible`, sin registrar el pedido. |
| `--puerto N` | Cambia el puerto (por defecto `8010`). |

## Datos de entrada

`datos/pedidos.xlsx` contiene ocho solicitudes de pedido. Cinco son válidas y tres son inválidas a propósito: una cantidad negativa, un código de medicamento inexistente y una cantidad vacía.
