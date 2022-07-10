# 목표달성 시스템 백엔드 
## 🤙 코딩 컨벤션

패키지명, 클래스명은 일반적인 관례대로 작성했어요.

일반적인 변수는 camelCase 방식으로 작성했고, Enum이나 정적상수는 SNAKE_CASE 방식으로 작성했어요.

API 구현시 최대한 RESTfull 하게 하려고 노력했어요.

## 🖥 서버환경

OS : AWS EC2 - Amazon Linux 2

DB : MariaDB 10.5.16 for Linux

Language : Java - openjdk 11.0.13 2021-10-19 LTS

Frameworks : Spring core 5.3.16, Spring Boot 2.6.4

## ⌨️ 협업 방식

깃허브를 통해 API 문서를 제공했어요.

Notion, Discord 를 통해 비대면으로 협업을 진행했어요.

## ⚡ 기술 스택

Spring Framework : 객체지향 프로그래밍으로 주어진 비즈니스 로직 구현에 집중할 수 있도록 Spring Framework를 사용했어요.

Spring Boot : Spring Framework의 단점인 복잡한 의존성 설정을 자동으로 해줌으로써 생산성을 높여주는 Spring Boot를 사용했어요.

## 🛠 이슈

1. 트랜젝션 설정 적용 안됨
    - 몇몇 기능의 경우 보다 작은 기능의 조합으로 이루어지는데, 각각의 작은 기능에서 어떤 오류가 발생한 경우 모든 작은 기능들이 실행되기 이전으로 상태를 되돌려야 합니다. 이를 위해 스프링에선 @`Transactional` 어노테이션을 제공합니다. 그러나, 이 설정이 제대로 적용되지 않는 현상이 있었습니다.
    - 해당 어노테이션이 적용된 메서드 내부에서 try-catch문이 동작하는 경우 트랜젝션 어노테이션이 제대로 동작하지 않는 경우가 있다는 정보를 찾았고, 이를 적용해 해결했습니다.
2. CORS 예외처리 설정 적용 안됨
    - 보안의 이유로 브라우저에서는 주 요청을 보내기 전에 예비 요청을 통해 CORS 검사를 하게 됩니다. 따라서, 프론트 어플리케이션이 동작하는 오리진과 서버(aws ec2)의 오리진이 달라 브라우저에서 아예 api 요청을 보내지 않고 에러를 발생시킵니다. 이를 해결하기 위해서는 백엔드에서 별도의 처리를 해주어야 하고, Spring 에선 `WebMvcConfigurer.addCorsMappings(CorsRegistry registry)` 메서드를 이요해 이 처리를 하게 됩니다. 그러나, 이 설정이 전혀 동작하지 않는 문제가 있었습니다.
    - 이 프로젝트에서 사용자의 인증,인가 작업을 처리하기 위해 Spring에서 제공하는 `Interceptor` 를 이용했습니다. 인터셉터를 통해 요청의 헤더에 포함되어 있는 인증 토큰을 추출하여 이를 이용해 사용자를 인증 하는 구조입니다.
    - 그러나, 브라우저에서 CORS 검사를 위해 보내는 예비요청(Preflight Request)에는 당연히 인증토큰이 포함되지 않고, 인터셉터 내부에서 NPE(NullPointerException)이 발생하게됩니다.
    - 인터셉터에서의 예외발생의 경우 별도로 처리를 하지 않았으므로 스프링이 자체적으로 처리(아마도 500 에러)를 하게되고 이 과정에서 Preflight 요청에 제대로 응답이 넘어가지 않았다고 볼 수 있었습니다.
    - 따라서 이를 해결하기 위해 인터셉터에서 Preflight 요청이 OPTIONS 메서드로 온다는 점을 이용해 별도로 인증 처리를 하지 않도록 하여 문제를 해결했습니다.
3. 복잡하고 반복된 코드가 많이 보이는 컨트롤러
    1. 개발 초기 진행 중의 컨트롤러 코드의 일부를 보면

        ```java
        @PostMapping("/cert/{goalId:[0-9]+}")
        public ResponseEntity<Certification> addCertificationByGoalId(@PathVariable long goalId, @RequestBody Certification certification, @RequestHeader("Authorization") String token){
            String goalOwnerEmail = JwtBuilder.getEmailFromJwt(token);
            try {
                certService.addCert(certification, goalOwnerEmail);
                return ResponseEntity.ok(certService.getCertificationByGoalId(goalId).orElseThrow());
            }catch (PermissionException e){
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }catch (DuplicateCertificationException e){
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }catch (DataAccessException e){
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }
        }
        
        @PutMapping("/cert/success/{goalId:[0-9]+}")
        public ResponseEntity<?> successVerification(@PathVariable long goalId,@RequestHeader("Authorization") String token){
            String requestEmail = JwtBuilder.getEmailFromJwt(token);
            try{
                verfiService.success(goalId,requestEmail);
                return ResponseEntity.ok().build();
            }catch (DataAccessException e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }catch (PermissionException e){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            }
        }
        ```

       중복된 예외 처리 코드가 많이 보입니다. 이를 해결하기 위해 스프링에서 제공하는 `ExceptionHandler` 를 이용했습니다. 아래는 이를 이용해 예외처리를 `SpringHandleExceptionHandler` 라는 클래스로 이관한 모습입니다.

       `SpringHandleExceptionHandler`

        ```java
        @RestControllerAdvice
        public class SpringHandleExceptionHandler {
        
            @ExceptionHandler(SpringHandledException.class)
            public ResponseEntity<?> handle(SpringHandledException exception){
                exception.printStackTrace();
                return exception.parseResponseEntity();
            }
        }
        ```

       GoalController 일부

        ```java
        @PostMapping("/cert/{goalId:[0-9]+}")
        public ResponseEntity<Certification> addCertificationByGoalId(@PathVariable long goalId, @RequestBody Certification certification, @RequestHeader("Authorization") String token){
            String goalOwnerEmail = JwtBuilder.getEmailFromJwt(token);
            try {
                certService.addCert(certification, goalOwnerEmail);
                return ResponseEntity.ok(certService.getCertificationByGoalId(goalId));
            }catch (DataAccessException e){
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }
        }
        
        @PutMapping("/cert/success/{goalId:[0-9]+}")
        public ResponseEntity<?> successVerification(@PathVariable long goalId,@RequestHeader("Authorization") String token){
            String requestEmail = JwtBuilder.getEmailFromJwt(token);
            try{
                verfiService.success(goalId,requestEmail);
                return ResponseEntity.ok().build();
            }catch (DataAccessException e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        ```


## 앞으로 개선할만한 것들
### 개발 속도를 위해 타협한 더티코드 정리
이번 프로젝트에는 백엔드 개발자 1명, 프론트엔드 개발자 3명이 참여했습니다. 따라서 넘처나는 수정요청과 기능추가 요청을 혼자 처리하다보니, 지금 작성중인 코드가 나중에 문제가 될 수 있음을 알면서도 넘어간 부분이 더러 있습니다. 그런 부분을 하나씩 천천히 수정해나갈 예정입니다.
- 이미지 저장 코드의 메서드 분리
    ```java
    public Announcement addAnnouncement(Announcement announcement) {
            String image = announcement.getImage();
            try {
                byte[] imageData = java.util.Base64.getDecoder().decode(image.substring(image.indexOf(",") + 1));
                String filenameExtension  = image.split(",")[0].split("/")[1].split(";")[0];
                String fileName = "announcement"+File.separator+announcement.getAnnouncementId();
                File imageFile = new File(fileName);
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageData));
                ImageIO.write(bufferedImage,filenameExtension,imageFile);
                announcement.setImage(fileName);
                String banner = announcement.getBannerImage();
                announcement.setBannerImage("announcement"+File.separator+"banner"+announcement.getAnnouncementId());
                long announcementId = adminRepository.insertAnnouncement(announcement);
                announcement.setBannerImage(banner);
                announcement.setAnnouncementId(announcementId);
                announcement.setDate(Timestamp.valueOf(LocalDateTime.now()));
                fileName = "announcement"+File.separator+announcement.getAnnouncementId();
                imageFile.renameTo(new File(fileName));
                System.out.println(fileName);
                announcement.setImage("");
                saveBannerImage(announcement);
                return announcement;
            }catch (IllegalArgumentException e){
                throw new SpringHandledException(HttpStatus.BAD_REQUEST,ErrorCode.UNKNOWN,"POST /api/admin/announcement","DataURI 이미지가 아닙니다.");
            } catch (IOException e) {
                throw new SpringHandledException(HttpStatus.BAD_REQUEST,ErrorCode.UNKNOWN,"POST /api/admin/announcement","DataURI 를 파일로 바꿀 수 없습니다.");
            }
        }
    ```
  딱 봐도 읽기 어려운 코드입니다. 이런 코드들이 프로젝트 구석구석에 존재합니다. 이런 코드를 수정할 예정입니다.
