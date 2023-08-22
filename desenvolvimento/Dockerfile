FROM python:3
RUN apt-get update && apt-get -y install ghostscript poppler-utils && apt-get clean
COPY . .
WORKDIR /src
RUN pip install --upgrade pip
RUN pip install -r requirements.txt