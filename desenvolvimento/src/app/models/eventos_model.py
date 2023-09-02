from app.utils.mongodb import get_mongo_db
from pymongo.collection import Collection

class EventosModel:
    def __init__(self, numero: int, descricao: str):
        self.numero = numero
        self.descricao = descricao
        self.collection = get_mongo_db().eventos

    def find_by_id(self, id: int):
        return self.collection.find_one({"id": id})

    def create_evento(self):
        evento = {
            "esis_codigo": self.numero,
            "descricao": self.descricao
        }
        return self.collection.insert_one(evento)
