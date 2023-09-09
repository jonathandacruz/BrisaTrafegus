import uuid
from app.utils.mongodb import get_mongo_db

class RegrasQuebradasModel:

    def __init__(self, regra: uuid, codigos: list[int], percentual: int ):
        self.regra = regra
        self.codigos = codigos
        self.percentual = percentual