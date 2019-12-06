from requests import get
from paths import *
from json import dump
from datetime import datetime

if not data_dir.exists():
    data_dir.mkdir()

def write_json(object, path):
    with open(path, 'w') as f:
        dump(object, f, indent=4)

print("Fetching item types", flush=True)
item_type_info = get("https://api.zotero.org/itemTypes").json()
write_json(item_type_info, item_types_file)

print("Fetching fields", flush=True)
fields = get("https://api.zotero.org/itemFields").json()
write_json(fields, fields_file)

print("Fetching item type fields", flush=True)
item_types = [obj['itemType'] for obj in item_type_info]
item_type_fields = {}
for item_type in item_types:
    print(item_type, end=" ", flush=True)
    fields = list(get(f"https://api.zotero.org/items/new?itemType={item_type}").json().keys())
    item_type_fields[item_type] = fields
write_json(item_type_fields, item_type_fields_file)

print("\nFetching creator types", flush=True)
creator_types = {}
for item_type in item_types:
    print(item_type, end=" ", flush=True)
    types = get(f"https://api.zotero.org/itemTypeCreatorTypes?itemType={item_type}").json()
    creator_types[item_type] = types
write_json(creator_types, creator_types_file)

print("\nFetching creator fields", flush=True)
creator_fields = get("https://api.zotero.org/creatorFields").json()
write_json(creator_fields, creator_fields_file)

with open(timestamp_file, 'w') as f:
    f.write(f"{datetime.now():%Y-%m-%d %H:%M:%S}")

print('\nDone')
