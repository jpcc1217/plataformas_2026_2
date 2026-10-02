from dotenv import load_dotenv
from pathlib import Path
import os

raiz = Path(__file__).parent.parent
load_dotenv(".env")

REQUERIDOS = ("PORTAL_USUARIO", "PORTAL_CLAVE")


lista_faltantes = [x for x in REQUERIDOS if not os.getenv(x)]
if lista_faltantes:
    raise SystemExit(f"Faltan variables de entorno requeridas: {lista_faltantes}")


PORTAL_URL = os.getenv("PORTAL_URL")
PORTAL_USUARIO = os.getenv("PORTAL_USUARIO")
PORTAL_CLAVE = os.getenv("PORTAL_CLAVE")
HEADLESS = os.getenv("BOT_HEADLESS").lower() == "true"
CARPETA_SALIDA = "salida"