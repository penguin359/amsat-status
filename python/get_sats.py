#!/usr/bin/env python3

#import xml.etree.ElementTree as etree
from pprint import pprint as pp
from itertools import chain

import lxml.html
import lxml.etree as etree
import requests

response = requests.get("https://amsat.org/status/")
response.raise_for_status()
body = lxml.html.fromstring(response.text)
satellites = list(chain([('DEMO-1', 'DEMO 1')], (
    (item.attrib['value'], item.text)
    for item
    in body.xpath("//select[@name = 'SatName']/option")
    if item.attrib['value'])))

root = etree.Element("resources")
id_array = etree.SubElement(root, "string-array", name="satellite_ids")
name_array = etree.SubElement(root, "string-array", name="satellite_names")

col1_len = max([len(item[0]) for item in satellites])
for item in iter(satellites):
    print(f"{item[0]:{col1_len}s} = {item[1]:s}")
    child = etree.SubElement(id_array, "item")
    child.text = item[0]
    etree.SubElement(name_array, "item").text = item[1]
# print(repr(satellites))
# pp(satellites)
xml = etree.tostring(root, pretty_print=True)
print(xml.decode('utf-8'))
with open('satellites.xml', 'wb') as f:
    f.write(xml)
