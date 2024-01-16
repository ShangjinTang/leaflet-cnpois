#!/usr/bin/env python3

import glob
import json

import pandas as pd

output_dir = "output"

file_list = glob.glob(f"{output_dir}/**/*.json")

df = pd.DataFrame()

for file in file_list:
    poi_type = file.split("/")[1]
    with open(file, "r") as f:
        data = json.load(f)
        try:
            temp_df = pd.json_normalize(data)
            temp_df.insert(0, "poi_type", poi_type)

            if "location.lat" in temp_df.columns and "location.lng" in temp_df.columns:
                temp_df[["location_lat", "location_lng"]] = temp_df[
                    ["location.lat", "location.lng"]
                ]
                temp_df.drop(["location.lat", "location.lng"], axis=1, inplace=True)

            columns_to_drop = ["street_id", "telephone", "detail", "uid"]
            temp_df.drop(columns_to_drop, axis=1, inplace=True)

            df = pd.concat([df, temp_df], ignore_index=True)
        except KeyError:
            continue

df.to_excel(f"{output_dir}/merged_data.xlsx", index=False)
