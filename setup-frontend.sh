#!/bin/bash

echo "🚀 Creating Frontend inside Spring Boot..."

BASE="src/main/resources/static"

# Create base folder
mkdir -p $BASE
cd $BASE || exit

# Root files
touch index.html login.html

# Pages
mkdir -p pages
touch pages/dashboard.html
touch pages/incidents.html
touch pages/create-incident.html
touch pages/incident-detail.html
touch pages/my-incidents.html
touch pages/sla-breached.html
touch pages/users.html

# Assets
mkdir -p assets/css assets/js assets/images

# CSS
touch assets/css/main.css
touch assets/css/components.css

# JS
touch assets/js/app.js
touch assets/js/api.js
touch assets/js/auth.js
touch assets/js/incidents.js
touch assets/js/create.js
touch assets/js/detail.js
touch assets/js/dashboard.js

# Components
mkdir -p components
touch components/sidebar.html
touch components/topbar.html

# Config
mkdir -p config
touch config/config.js

echo "✅ Frontend structure created successfully!"
