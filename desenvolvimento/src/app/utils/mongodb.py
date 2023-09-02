from flask import current_app
from pymongo import MongoClient

def get_mongo_db():
    return current_app.mongo.trafegus

def init_mongo_db(app):
    app.config['MONGO_URI'] = 'mongodb://mongo:27017/mongodb-trafegus'
    app.mongo = MongoClient(app.config['MONGO_URI'])
    app.db = app.mongo.trafegus