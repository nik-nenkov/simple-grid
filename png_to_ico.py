from PIL import Image
import sys

# Usage: python png_to_ico.py logo.png logo.ico
if len(sys.argv) != 3:
    print("Usage: python png_to_ico.py input.png output.ico")
    sys.exit(1)

input_path = sys.argv[1]
output_path = sys.argv[2]

img = Image.open(input_path)
# You can specify multiple sizes for the .ico file
img.save(output_path, format='ICO', sizes=[(256,256), (128,128), (64,64), (48,48), (32,32), (16,16)])
print(f"Converted {input_path} to {output_path}")
