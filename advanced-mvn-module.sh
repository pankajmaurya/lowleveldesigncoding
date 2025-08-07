#!/bin/bash

# Default values
DEFAULT_GROUP_ID="dev.lld.practice"
DEFAULT_ARCHETYPE_ARTIFACT_ID="maven-archetype-quickstart"
DEFAULT_ARCHETYPE_VERSION="1.4"

# Function to display usage
usage() {
    echo "Usage: $0 <artifactId> [groupId] [archetypeArtifactId] [archetypeVersion]"
    echo ""
    echo "Parameters:"
    echo "  artifactId           : Required. The artifact ID for the new module"
    echo "  groupId             : Optional. Default: $DEFAULT_GROUP_ID"
    echo "  archetypeArtifactId : Optional. Default: $DEFAULT_ARCHETYPE_ARTIFACT_ID"
    echo "  archetypeVersion    : Optional. Default: $DEFAULT_ARCHETYPE_VERSION"
    echo ""
    echo "Examples:"
    echo "  $0 taskscheduler"
    echo "  $0 userservice com.mycompany.services"
    echo "  $0 webapp com.example maven-archetype-webapp 1.4"
    exit 1
}

# Check if at least one argument (artifactId) is provided
if [ $# -eq 0 ]; then
    echo "Error: artifactId is required"
    usage
fi

# Parse arguments
ARTIFACT_ID="$1"
GROUP_ID="${2:-$DEFAULT_GROUP_ID}"
ARCHETYPE_ARTIFACT_ID="${3:-$DEFAULT_ARCHETYPE_ARTIFACT_ID}"
ARCHETYPE_VERSION="${4:-$DEFAULT_ARCHETYPE_VERSION}"

# Validate artifactId (basic validation)
if [[ ! "$ARTIFACT_ID" =~ ^[a-zA-Z][a-zA-Z0-9._-]*$ ]]; then
    echo "Error: Invalid artifactId '$ARTIFACT_ID'. Must start with a letter and contain only letters, numbers, dots, hyphens, and underscores."
    exit 1
fi

# Display configuration
echo "Generating Maven module with the following configuration:"
echo "  Group ID: $GROUP_ID"
echo "  Artifact ID: $ARTIFACT_ID"
echo "  Archetype Artifact ID: $ARCHETYPE_ARTIFACT_ID"
echo "  Archetype Version: $ARCHETYPE_VERSION"
echo ""

# Execute Maven archetype generation
mvn archetype:generate \
    -DgroupId="$GROUP_ID" \
    -DartifactId="$ARTIFACT_ID" \
    -DarchetypeArtifactId="$ARCHETYPE_ARTIFACT_ID" \
    -DarchetypeVersion="$ARCHETYPE_VERSION" \
    -DinteractiveMode=false

# Check if command was successful
if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Maven module '$ARTIFACT_ID' created successfully!"
    echo "📁 Module directory: ./$ARTIFACT_ID"
else
    echo ""
    echo "❌ Failed to create Maven module. Please check the error messages above."
    exit 1
fi
