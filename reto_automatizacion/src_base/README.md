# SIRH y datos del reto

## Arranque del portal

Requiere `uv` 0.8 o superior. Desde la carpeta `portal_sirh/`:

```bash
uv run python -m portal
```

El portal queda disponible en <http://127.0.0.1:8030>. Credenciales del operador: `operador.rpa` / `NominaSirh2026`.

Los datos viven en memoria: al detener el portal con `Ctrl+C` se pierden las novedades y los saldos vuelven a su valor inicial.

## Opciones

| Opción | Efecto |
| :--- | :--- |
| `--ui v2` | Interfaz rediseñada: cambian los `id`, las clases, la estructura HTML y el orden de los campos; se conservan las etiquetas visibles. |
| `--inestable` | Uno de cada tres envíos de novedad responde `503 Servicio temporalmente no disponible`, sin registrar nada. |
| `--puerto N` | Cambia el puerto (por defecto `8030`). |

## Datos

| Archivo | Uso |
| :--- | :--- |
| `datos/estructura_solicitudes.xlsx` | Crear la lista `<prefijo>-Solicitudes vacaciones` con la opción **Desde Excel**. |
| `datos/solicitudes_prueba.xlsx` | Las diez solicitudes de los escenarios de aceptación. |
| `datos/por_registrar.xlsx` | Plantilla de la cola de trabajo del robot, con la tabla `PorRegistrar`. |
