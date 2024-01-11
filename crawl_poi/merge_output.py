#!/usr/bin/env python3

import json
from pathlib import Path

import pandas as pd

# 存储所有 JSON 数据的列表
all_data = []

merge_data_types = ["大学"]

# 遍历文件夹
for merge_data_type in merge_data_types:
    base_dir = Path("output")
    province_dirs = base_dir.glob("*")  # 获取所有省份文件夹
    for province_dir in province_dirs:
        if not province_dir.is_dir():
            continue
        province = province_dir.name  # 获取省份名称
        city_dirs = province_dir.glob("*")  # 获取省份下的城市文件夹
        for city_dir in city_dirs:
            if not city_dir.is_dir():
                continue
            city = city_dir.name
            json_files = city_dir.glob(f"{merge_data_type}.json")  # 获取城市文件夹下的.json文件
            for json_file in json_files:
                university = json_file.stem
                with open(json_file, "r", encoding="utf-8") as file:
                    json_data = json.load(file)
                    for item in json_data:
                        item.insert(0, merge_data_type)  # 插入大学信息到列表首位
                        item.append(province)  # 添加省份信息
                        item.append(city)  # 添加城市信息
                    all_data.extend(json_data)  # 将数据合并到列表中

    df = pd.DataFrame(all_data, columns=["搜索关键字", "名称", "地址", "坐标", "省份", "城市"])

    output_excel = "output/merged.xlsx"
    output_csv = "output/merged.csv"
    output_json = "output/merged.json"
    df.to_excel(output_excel, index=False)
    df.to_csv(output_csv, index=False)
    with open(output_json, "w", encoding="utf-8") as file:
        df.to_json(file, orient="records", indent=2, force_ascii=False)

    print("data saved")
