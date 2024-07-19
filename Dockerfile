FROM openjdk:17
ADD target/WhatWish.jar WhatWish.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "WhatWish.jar"]