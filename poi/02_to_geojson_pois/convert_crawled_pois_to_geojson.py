#!/usr/bin/env python3

##
# @description This is only for baidu crawled data.
##

import argparse
import json
import os
import sys
from pathlib import Path

from coordinate_coverter import bd09towgs84

data = []


def main(args):
    input_json_paths = args.input_dir.glob("**/*.json")
    converted_json_count = 0
    for input_json_path in input_json_paths:
        with open(input_json_path) as f:
            data = json.load(f)

        features = []
        for item in data:
            name = item[0]
            address = item[1]
            coordinates = list(map(float, item[2].split(",")))
            bd09_lng, bd09_lat = coordinates
            wgs84_lng, wgs84_lat = bd09towgs84(bd09_lng, bd09_lat)

            feature = {
                "type": "Feature",
                "properties": {
                    "name": name,
                    "address": address,
                    "tag": Path(input_json_path).stem,
                },
                "geometry": {"type": "Point", "coordinates": [wgs84_lng, wgs84_lat]},
            }

            features.append(feature)

        geojson_content = {"type": "FeatureCollection", "features": features}

        input_json_path_relative = input_json_path.relative_to(args.input_dir)
        # keep the same directory structure in output dir as input dir
        output_geojson_path = args.output_dir / input_json_path_relative.with_suffix(
            ".geojson"
        )
        output_geojson_path.parent.mkdir(parents=True, exist_ok=True)

        json_data = json.dumps(geojson_content, ensure_ascii=False)
        with open(output_geojson_path, "w", encoding="utf-8") as f:
            converted_json_count = converted_json_count + 1
            f.write(json_data)
    print(f"Converted {converted_json_count} raw json files to geojson files")


if __name__ == "__main__":
    parser = argparse.ArgumentParser()

    parser.add_argument(
        "-i",
        "--input_dir",
        default=Path(__file__).resolve().parent / "../output/crawled_raw_pois",
        help="input directory",
    )

    parser.add_argument(
        "-o",
        "--output_dir",
        default=Path(__file__).resolve().parent / "../output/geojson_pois",
        help="output directory",
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
