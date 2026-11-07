#!/usr/bin/env bash
# Τρέχει μία φορά, όταν δημιουργείται το dev container / codespace.
set -euo pipefail

TOMCAT_VERSION="11.0.27"
TOMCAT_DIR="$HOME/apache-tomcat-${TOMCAT_VERSION}"
TOMCAT_LINK="$HOME/tomcat"          # = $CATALINA_HOME
TARBALL="apache-tomcat-${TOMCAT_VERSION}.tar.gz"

# ---- 1. Apache Tomcat ----
if [ ! -d "$TOMCAT_DIR" ]; then
  echo ">> Εγκατάσταση Apache Tomcat ${TOMCAT_VERSION} στο ${TOMCAT_DIR}"
  tmp="$(mktemp -d)"
  # Πρώτα από το CDN· αν η έκδοση έχει αποσυρθεί από εκεί, από το μόνιμο archive της Apache
  curl -fsSL -o "$tmp/$TARBALL" "https://dlcdn.apache.org/tomcat/tomcat-11/v${TOMCAT_VERSION}/bin/${TARBALL}" \
    || curl -fsSL -o "$tmp/$TARBALL" "https://archive.apache.org/dist/tomcat/tomcat-11/v${TOMCAT_VERSION}/bin/${TARBALL}"
  tar -xzf "$tmp/$TARBALL" -C "$HOME"
  rm -rf "$tmp"
fi
ln -sfn "$TOMCAT_DIR" "$TOMCAT_LINK"
chmod +x "$TOMCAT_LINK"/bin/*.sh

# ---- 2. Προ-λήψη Maven dependencies & plugins (για να είναι γρήγορο το πρώτο build) ----
if [ -f webapp1/pom.xml ]; then
  echo ">> Λήψη Maven dependencies για το webapp1"
  # go-offline: plugins (π.χ. Jetty) και dependencies.
  # package: ένα πραγματικό build, γιατί κάποια πράγματα (π.χ. ο JUnit provider του surefire)
  #          κατεβαίνουν μόνο όταν τρέξουν τα tests. Το clean στο τέλος σβήνει το target/.
  mvn -B -q -f webapp1/pom.xml dependency:go-offline package clean \
    || echo "!! Η προ-λήψη απέτυχε (δεν πειράζει, θα γίνει στο πρώτο build)"
fi

echo
echo "Έτοιμο!"
echo "  java     : $(java -version 2>&1 | head -1)"
echo "  mvn      : $(mvn -v | head -1)"
echo "  Tomcat   : ${CATALINA_HOME:-$TOMCAT_LINK}  ($("$TOMCAT_LINK"/bin/version.sh 2>/dev/null | grep 'Server number' | awk '{print $3}'))"
