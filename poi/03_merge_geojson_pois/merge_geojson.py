#!/usr/bin/env python3

import argparse
import json
import os
import sys
from pathlib import Path


def main(args):
    file_paths = Path(args.input_dir).glob("**/*.geojson")

    merged_features = []

    for file_path in file_paths:
        with open(file_path) as f:
            data = json.load(f)

        features = data["features"]
        for feature in features:
            feature["properties"]["province"] = file_path.parents[1].name
            feature["properties"]["city"] = file_path.parents[0].name

        merged_features.extend(features)

    merged_data_content = {"type": "FeatureCollection", "features": merged_features}

    args.output_path.parent.mkdir(parents=True, exist_ok=True)
    merged_data = json.dumps(merged_data_content, ensure_ascii=False)
    with open(args.output_path, "w", encoding="utf-8") as f:
        f.write(merged_data)


if __name__ == "__main__":
    parser = argparse.ArgumentParser()

    parser.add_argument(
        "-i",
        "--input_dir",
        default=Path(__file__).resolve().parent / "../output/geojson_pois",
        help="input directory",
    )

    parser.add_argument(
        "-o",
        "--output_path",
        default=Path(__file__).resolve().parent
        / "../output/geojson_pois_merged/pois.geojson",
        help="output merged file path",
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
