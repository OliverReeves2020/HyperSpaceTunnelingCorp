# Use the official Tomcat base image
FROM tomcat:latest

# Set the container name
LABEL container.name="HyperSpace"

# Remove the default ROOT application
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copy the application WAR file into the container's webapps directory
COPY HyperSpaceTunnelingCorp-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080
EXPOSE 8080

# Set the default command to start Tomcat
CMD ["catalina.sh", "run"]
