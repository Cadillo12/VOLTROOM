import os
import glob
import re

pages_dir = r"c:\Users\YERSON CADILLO\OneDrive - Universidad Tecnologica del Peru\CICLO VII 2026\CURSO INTEGRADOR I SISTEMAS SOTFWARE\VOLTROOM\voltroom-frontend\src\pages"

jsx_files = glob.glob(os.path.join(pages_dir, "*.jsx"))

for file_path in jsx_files:
    if "DashboardPage.jsx" in file_path or "LoginPage.jsx" in file_path:
        continue
        
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()
        
    # Check if file has the shapes wrapper
    if "{/* Background Abstract Shapes */}" in content:
        # Pattern to remove the shapes and the opening wrapper
        pattern_open = r"\s*\{\/\* Background Abstract Shapes \*\/\}[\s\S]*?<div style=\{\{ position: 'relative', zIndex: 10 \}\}>\s*"
        
        new_content = re.sub(pattern_open, "\n            ", content)
        
        # We need to remove the matching closing </div>
        # It's usually the second to last </div> before the final </div> of dashboard-container
        # An easier way is just to replace the last two </div> with one </div>, assuming standard format.
        # Let's find the last occurrence of "</div>\n        </div>" or similar
        pattern_close = r"</div>\s*</div>\s*\);\s*}\s*$"
        new_content = re.sub(pattern_close, "</div>\n    );\n}", new_content)
        
        with open(file_path, "w", encoding="utf-8") as f:
            f.write(new_content)
        print(f"Cleaned {os.path.basename(file_path)}")
