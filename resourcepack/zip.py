import os
import zipfile

source = "mekanism"
output = "mekanism_pack.zip"

with zipfile.ZipFile(output, 'w', zipfile.ZIP_DEFLATED) as zipf:
    for root, _, files in os.walk(source):
        for file in files:
            full_path = os.path.join(root, file)
            zipf.write(full_path, os.path.relpath(full_path, source))
