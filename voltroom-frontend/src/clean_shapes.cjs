const fs = require('fs');
const path = require('path');

const pagesDir = __dirname + '/pages';

fs.readdirSync(pagesDir).forEach(file => {
    if (file.endsWith('.jsx') && file !== 'DashboardPage.jsx' && file !== 'LoginPage.jsx') {
        const filePath = path.join(pagesDir, file);
        let content = fs.readFileSync(filePath, 'utf-8');
        
        if (content.includes('{/* Background Abstract Shapes */}')) {
            const patternOpen = /\s*\{\/\* Background Abstract Shapes \*\/\}[\s\S]*?<div style=\{\{ position: 'relative', zIndex: 10 \}\}>\s*/g;
            let newContent = content.replace(patternOpen, '\n            ');
            
            const patternClose = /<\/div>\s*<\/div>\s*\);\s*}\s*$/;
            newContent = newContent.replace(patternClose, '</div>\n    );\n}');
            
            fs.writeFileSync(filePath, newContent, 'utf-8');
            console.log('Cleaned ' + file);
        }
    }
});
