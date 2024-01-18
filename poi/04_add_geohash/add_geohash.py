#!/usr/bin/env python3

import argparse
import json
import os
import sys
from pathlib import Path

import geohash_tools


def main(args):
    with open(args.input_path) as f:
        data = json.load(f)

    for feature in data["features"]:
        coordinates = feature["geometry"]["coordinates"]
        latitude, longitude = coordinates[1], coordinates[0]
        geohash_code = geohash_tools.encode(latitude, longitude, precision=12)
        feature["properties"]["geohash"] = geohash_code

    args.output_path.parent.mkdir(parents=True, exist_ok=True)
    merged_data = json.dumps(data, ensure_ascii=False)
    with open(args.output_path, "w", encoding="utf-8") as f:
        f.write(merged_data)


if __name__ == "__main__":
    parser = argparse.ArgumentParser()

    parser.add_argument(
        "-i",
        "--input_path",
        default=Path(__file__).resolve().parent
        / "../output/geojson_pois_merged/pois.geojson",
        help="input geojson file path",
    )

    parser.add_argument(
        "-o",
        "--output_path",
        default=Path(__file__).resolve().parent
        / "../output/geojson_pois_merged/pois_with_geohash.geojson",
        help="output geojson file path",
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
