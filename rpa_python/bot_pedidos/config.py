from dotenv import load_dotenv
from pathlib import Path
import os

raiz = Path(__file__).parent.parent
load_dotenv(".env")

PORTAL_URL = os.getenv("PORTAL_URL")
PORTAL_USUARIO = os.getenv("PORTAL_USUARIO")
PORTAL_CLAVE = os.getenv("PORTAL_CLAVE")
HEADLESS = os.getenv("BOT_HEADLESS").lower() == "true"
CARPETA_SALIDA = "salida"