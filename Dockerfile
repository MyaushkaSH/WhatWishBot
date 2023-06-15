FROM openjdk:17
ADD target/WhatWishBot-0.0.1-SNAPSHOT.jar WhatWishBot-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "WhatWishBot-0.0.1-SNAPSHOT.jar"]