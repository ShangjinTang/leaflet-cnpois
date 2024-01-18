#!/usr/bin/env python3
import argparse
import json
import os
import random
import re
import sys
import time
from pathlib import Path

import requests
from bs4 import BeautifulSoup
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver import Chrome, ChromeOptions
from selenium.webdriver.common.by import By


def get_provinces_cities():
    url = "http://api.map.baidu.com/lbsapi/getpoint/index.html"
    response = requests.get(url)
    html_content = response.content.decode("utf-8")
    soup = BeautifulSoup(html_content, "html.parser")
    provinces_cities = {}
    # Extract province and city information
    rows = soup.find_all("tr")

    # 直辖市 (municipality)
    municipality_td = soup.find("td", string=re.compile("直辖市"))
    if municipality_td:
        municipality_find = municipality_td.find_next_sibling("td")
        if municipality_find:
            municipality_cities = municipality_find.text.strip().split()
            # 直辖市没有市，将其省也设置为相同的，如："北京": ["北京"]
            for municipality_city in municipality_cities:
                provinces_cities.update({municipality_city: [municipality_city]})

    # 非直辖市
    CANNOT_CLICKED_CITIES = {
        "神农架林区",
        "襄樊",
        "铜仁地区",
        "毕节地区",
        "山南地区",
        "保亭",
        "昌江",
        "那曲地区",
        "延边",
        "昌都地区",
        "陵水",
        "海东地区",
        "日喀则地区",
        "林芝地区",
        "琼中",
        "白沙",
        "乐东",
        "西双版纳",
        "吐鲁番地区",
        "哈密地区",
    }
    for row in rows:
        try:
            province = row.find("a", class_="black").text.strip()
            cities = []
            for city in row.find_all("a", onclick="goCity(this)"):
                if city.text and city.text != province:
                    if city.text not in CANNOT_CLICKED_CITIES:
                        cities.append(city.text)
            provinces_cities.update({province: cities})
        except AttributeError as e:
            pass

    return provinces_cities


def crawl_data(query_str, open_browser, output_dir):
    chrome_options = ChromeOptions()  # 创建浏览器参数设置的对象
    if not open_browser:
        chrome_options.add_argument("--headless")  # 设置参数--headless，运行时不会弹出浏览器
    """
    将参数设置对象传递给浏览器驱动类的构造方法的默认参数options，实例化一个浏览器驱动对象
    with关键字用来确保不管程序是不是有问题，每次都能关闭浏览器和浏览器驱动程序
    """
    with Chrome(options=chrome_options) as browser:
        provinces_cities = get_provinces_cities()
        for province, cities in provinces_cities.items():
            for city in cities:
                city_path = output_dir / province / city
                if Path.exists(city_path / f"{query_str}.json"):
                    continue
                if not Path.exists(city_path):
                    Path.mkdir(city_path, parents=True)

                browser.get(
                    "http://api.map.baidu.com/lbsapi/getpoint/index.html"
                )  # 发送请求
                browser.find_element(by=By.ID, value="curCityText").click()  # 找到更换城市并点击
                time.sleep(random.random() + 1)  # 等待一段时间
                browser.find_element(by=By.LINK_TEXT, value=city).click()  # 找到城市并点击
                time.sleep(random.random() + 1)  # 等待一段时间
                browser.find_element(by=By.ID, value="localvalue").send_keys(
                    query_str
                )  # 找到输入框，输入关键字公司
                browser.find_element(
                    by=By.ID, value="localsearch"
                ).click()  # 点击百度一下进行搜索
                pattern = re.compile(
                    r'<div id="no_\d".*?>\s+<a href="javascript:void\(0\)" title="(.*?)">.*?</a>\s+<p>地址：(.*?)\s+'
                    r".*?坐标：(.*?)\s+</p>",
                    re.S,
                )  # 正则表达式

                time.sleep(random.random() + 1)  # 等待一段时间
                result_list = []
                result_list.extend(pattern.findall(browser.page_source))

                # 一直点击下一页，直到找不到下一页为止
                while True:
                    try:
                        browser.find_element(
                            by=By.LINK_TEXT, value="下一页"
                        ).click()  # 找到下一页并点击
                        time.sleep(random.random() + 1)  # 等待一段时间
                        result_list.extend(pattern.findall(browser.page_source))
                    except NoSuchElementException:
                        break
                json_data = json.dumps(result_list, ensure_ascii=False)

                with open(
                    city_path / f"{query_str}.json",
                    "w",
                    encoding="utf-8",
                ) as f:
                    f.write(json_data)

                print(f"Crawled: {province}/{city}/{query_str}.json")


def main(args):
    for query_str in args.query_strs:
        crawl_data(query_str, args.open_browser, args.output_dir)


if __name__ == "__main__":
    parser = argparse.ArgumentParser()

    parser.add_argument(
        "-o",
        "--output_dir",
        default=Path(__file__).resolve().parent / "../output/crawled_raw_pois",
        help="output directory",
    )

    parser.add_argument(
        "-q",
        "--query_strs",
        default=["大学", "高铁站"],
    )

    parser.add_argument("--open_browser", default=False, action="store_true")

    # You can use mutex groups for conflicted commands
    # group = parser.add_mutually_exclusive_group()
    # group.add_argument('--foo', action='store_true')
    # group.add_argument('--bar', action='store_false')

    # You can use subparsers for complex commands
    # subparsers = parser.add_subparsers(title="subcommands")

    try:
        args = parser.parse_args()
        main(args)
    except KeyboardInterrupt:
        print("Interrupted")
        try:
            sys.exit(0)
        except SystemExit:
            os._exit(0)
