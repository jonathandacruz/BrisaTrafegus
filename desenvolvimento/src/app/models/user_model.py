from app.utils.mongodb import get_mongo_db
from pymongo.collection import Collection

class UserModel:
    def __init__(self, app):
        self.collection = get_mongo_db().users

    def find_by_username(self, username: str):
        return self.collection.find_one({"username": username})

    def create_user(self, username: str, hashed_password: str):
        user_data = {
            "username": username,
            "password": hashed_password
        }
        return self.collection.insert_one(user_data)
