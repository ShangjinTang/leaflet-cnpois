#!/usr/bin/env python3

import argparse
import json
import os
import sys
from pathlib import Path

from sqlalchemy import Column, Float, Integer, String, create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker


def main(args):
    db_file = args.output_file

    db_file.parent.mkdir(parents=True, exist_ok=True)

    if db_file.exists():
        os.remove(db_file)
    db_file.touch()

    engine = create_engine(f"sqlite:///{db_file}?charset=utf8", echo=True)

    Base = declarative_base()

    class Geodata(Base):
        __tablename__ = args.table_name
        id = Column(Integer, primary_key=True)
        name = Column(String())
        province = Column(String())
        city = Column(String())
        address = Column(String())
        tag = Column(String())
        # longitude = Column(Float)
        # latitude = Column(Float)
        coordinate = Column(String())
        geohash = Column(String())

    Base.metadata.create_all(engine)

    Session = sessionmaker(bind=engine)
    session = Session()

    with open(args.input_file, encoding="utf-8") as f:
        json_data = json.load(f)

    for feature in json_data["features"]:
        geodata = Geodata(
            name=feature["properties"]["name"],
            province=feature["properties"]["province"],
            city=feature["properties"]["city"],
            address=feature["properties"]["address"],
            tag=feature["properties"]["tag"],
            # longitude=feature["geometry"]["coordinates"][0],
            # latitude=feature["geometry"]["coordinates"][1],
            coordinate=str(feature["geometry"]["coordinates"]),
            geohash=feature["properties"]["geohash"],
        )

        session.add(geodata)

    session.commit()
    session.close()


if __name__ == "__main__":
    parser = argparse.ArgumentParser()

    parser.add_argument(
        "-i",
        "--input_file",
        default=Path(__file__).resolve().parent
        / "../output/geojson_pois_merged/pois_with_geohash.geojson",
        help="input geojson file path",
    )

    parser.add_argument(
        "-o",
        "--output_file",
        default=Path(__file__).resolve().parent
        / "../output/sqlite3_pois/pois_with_geohash.sqlite",
        help="output sqlite3 database file path",
    )

    parser.add_argument(
        "--table_name",
        default="pois_cn_all",
        help="table name in output sqlite3 database",
    )

    try:
        args = parser.parse_args()
        main(args)
    except KeyboardInterrupt:
        print("Interrupted")
        try:
            sys.exit(0)
        except SystemExit:
            os._exit(0)
