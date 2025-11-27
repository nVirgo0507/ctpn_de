package org.ctpn.chungtayphongngua.service;

import org.ctpn.chungtayphongngua.entity.*;
import org.ctpn.chungtayphongngua.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final CourseRepository courseRepository;
    private final UserProfileRepository userProfileRepository;
    private final AssessmentTypeRepository assessmentTypeRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CategoryRepository categoryRepository;
    private final BlogPostRepository blogPostRepository;
    private final ConsultationService consultationService;

    public DataSeeder(UserRepository userRepository, RoleRepository roleRepository,
            UserRoleRepository userRoleRepository, CourseRepository courseRepository,
            UserProfileRepository userProfileRepository, AssessmentTypeRepository assessmentTypeRepository,
            LessonRepository lessonRepository, EnrollmentRepository enrollmentRepository,
            CategoryRepository categoryRepository, BlogPostRepository blogPostRepository,
            ConsultationService consultationService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.courseRepository = courseRepository;
        this.userProfileRepository = userProfileRepository;
        this.assessmentTypeRepository = assessmentTypeRepository;
        this.lessonRepository = lessonRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.categoryRepository = categoryRepository;

        this.blogPostRepository = blogPostRepository;
        this.consultationService = consultationService;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            logger.info("Data already seeded. Skipping.");
            return;
        }
        logger.info("Starting data seeding...");
        try {
            seedRoles();
            seedUsersAndProfiles();
            seedCoursesAndLessons();
            seedEnrollments();
            seedAssessmentTypes();
            seedBlogContent();

            consultationService.seedAvailabilityForConsultants();
            logger.info("Data seeding completed successfully.");
        } catch (Exception e) {
            logger.error("Error during data seeding. Rolling back transaction.", e);
            throw e; // Re-throw to ensure transaction rollback
        }
    }

    private void seedRoles() {
        logger.info("Seeding roles...");
        List<Role> roles = Arrays.asList(
                new Role("GUEST", "Anonymous users with limited access"),
                new Role("MEMBER", "Registered users with access to assessments and courses"),
                new Role("STAFF", "Staff members with content management access"),
                new Role("CONSULTANT", "Volunteer consultants providing counseling services"),
                new Role("MANAGER", "Managers with oversight and reporting access"),
                new Role("ADMIN", "System administrators with full access"));
        roleRepository.saveAll(roles);
        logger.info("Roles seeded.");
    }

    private void seedUsersAndProfiles() {
        logger.info("Seeding users and profiles...");
        Role adminRole = roleRepository.findByRoleName("ADMIN").orElseThrow();
        Role memberRole = roleRepository.findByRoleName("MEMBER").orElseThrow();
        Role staffRole = roleRepository.findByRoleName("STAFF").orElseThrow();
        Role consultantRole = roleRepository.findByRoleName("CONSULTANT").orElseThrow();
        Role managerRole = roleRepository.findByRoleName("MANAGER").orElseThrow();

        createUser("admin@ctpn.org", "Nguyen Van Quan", "0901234567", LocalDate.of(1985, 5, 15), adminRole);
        User manager = createUser("manager@ctpn.org", "Tran Thi Hanh", "0901234568", LocalDate.of(1988, 8, 20),
                managerRole);
        User staff = createUser("staff@ctpn.org", "Le Minh Tuan", "0901234569", LocalDate.of(1990, 12, 3), staffRole);

        User consultant1 = createUser("consultant1@ctpn.org", "Bac si Pham Thi Lan", "0901234570",
                LocalDate.of(1982, 3, 22), consultantRole);
        createConsultantProfile(consultant1, "Bác sĩ chuyên khoa tâm thần...",
                Arrays.asList("addiction_counseling", "family_therapy"), 12, 4.8, 156, 234);

        User consultant2 = createUser("consultant2@ctpn.org", "Thac si Vu Dinh Nam", "0901234571",
                LocalDate.of(1979, 11, 18), consultantRole);
        createConsultantProfile(consultant2, "Thạc sĩ Tâm lý học...", Arrays.asList("youth_support", "family_therapy"),
                8, 4.9, 89, 145);

        createUser("member1@ctpn.org", "Nguyen Thanh Hai", "0901234573", LocalDate.of(1995, 1, 25), memberRole);
        createUser("member2@ctpn.org", "Tran Thi Huong", "0901234574", LocalDate.of(2005, 9, 14), memberRole);
        createUser("member3@ctpn.org", "Le Van Dung", "0901234575", LocalDate.of(1992, 6, 30), memberRole);
        logger.info("Users and profiles seeded.");
    }

    private User createUser(String email, String fullName, String phone, LocalDate dob, Role role) {
        User user = new User(email, fullName);
        user.setPasswordHash("$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi");
        user.setPhone(phone);
        user.setDateOfBirth(dob);
        user.setIsVerified(true);
        userRepository.save(user);
        userRoleRepository.save(new UserRole(user, role));
        return user;
    }

    private void createConsultantProfile(User consultant, String bio, List<String> specializations, int exp,
            double rating, int reviews, int sessions) {
        UserProfile profile = new UserProfile();
        profile.setUser(consultant);
        profile.setBio(bio);
        profile.setSpecializations(specializations);
        profile.setExperienceYears(exp);
        profile.setRating(rating);
        profile.setTotalReviews(reviews);
        profile.setTotalSessions(sessions);
        profile.setConsultant(true);
        userProfileRepository.save(profile);
    }

    private void seedCoursesAndLessons() {
        logger.info("Seeding courses and lessons...");
        User staff = userRepository.findByEmail("staff@ctpn.org").orElseThrow();
        User consultant1 = userRepository.findByEmail("consultant1@ctpn.org").orElseThrow();
        User consultant2 = userRepository.findByEmail("consultant2@ctpn.org").orElseThrow();
        User manager = userRepository.findByEmail("manager@ctpn.org").orElseThrow();

        // Course 1
        Course course1 = createCourse("Hiểu biết cơ bản về phòng chống tệ nạn xã hội",
                "Kiến thức cơ bản về tác hại của ma túy, rượu bia và các chất gây nghiện.", staff,
                "Giáo dục phòng ngừa", "beginner", 4, 50, 4.5, 123);
        createLesson(course1, "Bài 1: Tổng quan về tệ nạn xã hội",
                "Giới thiệu về các loại chất gây nghiện phổ biến tại Việt Nam.", 45, 1);
        createLesson(course1, "Bài 2: Tác hại của ma túy",
                "Phân tích chi tiết các tác hại của ma túy đối với sức khỏe thể chất và tinh thần.", 50, 2);

        // Course 2
        createCourse("Hỗ trợ gia đình có người nghiện chất",
                "Hướng dẫn gia đình cách nhận biết, ứng phó và hỗ trợ người thân.", consultant1, "Hỗ trợ gia đình",
                "intermediate", 8, 30, 4.7, 89);

        // Course 3
        createCourse("Kỹ năng sống và phòng chống tệ nạn cho thanh thiếu niên",
                "Giáo dục kỹ năng sống, nâng cao nhận thức và khả năng từ chối.", consultant2,
                "Giáo dục thanh thiếu niên", "beginner", 6, 40, 4.8, 156);

        // Course 4: New Drug-Specific Course
        Course course4 = createCourse("Tác hại của Ma túy đá và Heroin",
                "Phân tích chuyên sâu về tác động của Methamphetamine và Heroin lên não bộ và cơ thể.", consultant1,
                "Giáo dục chuyên sâu", "intermediate", 5, 40, 4.9, 110);
        createLesson(course4, "Bài 1: Methamphetamine (Ma túy đá)", "Cơ chế hoạt động, tác hại tức thời và lâu dài.",
                60, 1);
        createLesson(course4, "Bài 2: Heroin", "Nguồn gốc, cách gây nghiện và các biến chứng nguy hiểm.", 60, 2);

        // Course 5: New Legal Course
        Course course5 = createCourse("Pháp luật Việt Nam về Ma túy",
                "Tổng quan các quy định pháp luật hiện hành về tàng trữ, mua bán và sử dụng ma túy.", manager,
                "Pháp luật và Chính sách", "beginner", 3, 100, 4.6, 95);
        createLesson(course5, "Bài 1: Các tội danh liên quan đến ma túy",
                "Phân tích các điều khoản trong Bộ luật Hình sự.", 45, 1);
        createLesson(course5, "Bài 2: Quy trình xử lý pháp lý", "Các bước xử lý khi bị phát hiện liên quan đến ma túy.",
                45, 2);

        // Course 6: New Relapse Prevention Course
        Course course6 = createCourse("Kỹ năng phòng chống tái nghiện",
                "Cung cấp các công cụ và kỹ năng cần thiết để duy trì sự phục hồi lâu dài.", consultant2,
                "Phục hồi và Tái hòa nhập", "advanced", 10, 25, 4.9, 80);
        createLesson(course6, "Bài 1: Nhận diện các tác nhân gây tái nghiện",
                "Học cách xác định các tình huống, cảm xúc và con người có nguy cơ cao.", 75, 1);
        createLesson(course6, "Bài 2: Xây dựng kế hoạch đối phó",
                "Tạo một kế hoạch hành động cụ thể khi đối mặt với cám dỗ.", 90, 2);

        logger.info("Courses and lessons seeded.");
    }

    private Course createCourse(String title, String desc, User inst, String cat, String level, int dur, int max,
            double rating, int reviews) {
        Course course = new Course(title, desc, inst, cat, level);
        course.setDurationHours(dur);
        course.setMaxStudents(max);
        course.setRating(BigDecimal.valueOf(rating));
        course.setTotalReviews(reviews);
        course.setPrice(BigDecimal.ZERO);
        return courseRepository.save(course);
    }

    private void createLesson(Course course, String title, String content, int duration, int order) {
        Lesson lesson = new Lesson();
        lesson.setCourse(course);
        lesson.setTitle(title);
        lesson.setContent(content);
        lesson.setDuration(duration);
        lesson.setOrderIndex(order);
        lessonRepository.save(lesson);
    }

    private void seedEnrollments() {
        logger.info("Seeding enrollments...");
        User member1 = userRepository.findByEmail("member1@ctpn.org").orElseThrow();
        Course course1 = courseRepository.findByTitle("Hiểu biết cơ bản về phòng chống tệ nạn xã hội").orElseThrow();
        Course course2 = courseRepository.findByTitle("Hỗ trợ gia đình có người nghiện chất").orElseThrow();

        createEnrollment(member1, course1, 75, 85.5, "active");
        createEnrollment(member1, course2, 100, 92.0, "completed");
        logger.info("Enrollments seeded.");
    }

    private void createEnrollment(User user, Course course, int progress, double score, String status) {
        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollment.setProgress(progress);
        enrollment.setFinalScore(BigDecimal.valueOf(score));
        enrollment.setStatus(status);
        enrollmentRepository.save(enrollment);
    }

    private void seedAssessmentTypes() {
        logger.info("Seeding assessment types...");
        String assistJson = """
                {
                    "instructions": "Hãy trả lời thành thật về việc sử dụng các chất kích thích trong 3 tháng qua. Thông tin của bạn sẽ được bảo mật.",
                    "scoring": {
                        "low": {"min": 0, "max": 10, "description": "Nguy cơ thấp"},
                        "moderate": {"min": 11, "max": 26, "description": "Nguy cơ trung bình"},
                        "high": {"min": 27, "max": 100, "description": "Nguy cơ cao"}
                    },
                    "options": {
                        "frequency": ["Không bao giờ", "Một hoặc hai lần", "Hàng tháng", "Hàng tuần", "Hàng ngày"]
                    },
                    "questions": [
                        {"id": "q1", "text": "Trong 3 tháng qua, bạn có sử dụng rượu bia không?", "type": "frequency", "category": "alcohol", "weight": 2},
                        {"id": "q2", "text": "Trong 3 tháng qua, bạn có sử dụng thuốc lá không?", "type": "frequency", "category": "tobacco", "weight": 2},
                        {"id": "q3", "text": "Trong 3 tháng qua, bạn có sử dụng ma túy không?", "type": "frequency", "category": "drugs", "weight": 3},
                        {"id": "q4", "text": "Bạn có cảm thấy cần phải sử dụng nhiều hơn để có cùng cảm giác?", "type": "frequency", "category": "tolerance", "weight": 4},
                        {"id": "q5", "text": "Bạn có muốn cắt bỏ hoặc giảm việc sử dụng chất kích thích?", "type": "frequency", "category": "control", "weight": 3}
                    ]
                }
                """;

        String crafftJson = """
                {
                    "instructions": "Hãy trả lời có/không cho các câu hỏi sau đây. Thông tin này sẽ giúp chúng tôi đưa ra lời khuyên phù hợp.",
                    "scoring": {
                        "low": {"min": 0, "max": 1, "description": "Nguy cơ thấp"},
                        "high": {"min": 2, "max": 6, "description": "Nguy cơ cao"}
                    },
                    "options": {
                        "yesno": ["Không", "Có"]
                    },
                    "questions": [
                        {"id": "q1", "text": "Bạn có bao giờ lái xe khi đã sử dụng chất kích thích?", "type": "yesno", "category": "car", "weight": 1},
                        {"id": "q2", "text": "Bạn có sử dụng chất kích thích để thư giãn hay tự tin hơn?", "type": "yesno", "category": "relax", "weight": 1},
                        {"id": "q3", "text": "Bạn có sử dụng chất kích thích khi một mình?", "type": "yesno", "category": "alone", "weight": 1},
                        {"id": "q4", "text": "Bạn có quên những gì đã làm khi sử dụng chất kích thích?", "type": "yesno", "category": "forget", "weight": 1},
                        {"id": "q5", "text": "Gia đình hay bạn bè có nói bạn nên giảm sử dụng chất kích thích?", "type": "yesno", "category": "family", "weight": 1},
                        {"id": "q6", "text": "Bạn có gặp rắc rối khi sử dụng chất kích thích?", "type": "yesno", "category": "trouble", "weight": 1}
                    ]
                }
                """;

        createAssessmentType("ASSIST", "adult", "WHO Alcohol, Smoking and Substance Involvement Screening Test",
                "frequency_scoring", assistJson);
        createAssessmentType("CRAFFT", "youth", "CeASAR CRAFFT Screening Tool", "binary_scoring", crafftJson);
        logger.info("Assessment types seeded.");
    }

    private void createAssessmentType(String name, String ageGroup, String desc, String scoring, String json) {
        AssessmentType assessmentType = new AssessmentType(name, ageGroup, desc, scoring, json);
        assessmentTypeRepository.save(assessmentType);
    }

    private void seedBlogContent() {
        logger.info("Seeding blog content...");
        User staff = userRepository.findByEmail("staff@ctpn.org").orElseThrow();
        User consultant1 = userRepository.findByEmail("consultant1@ctpn.org").orElseThrow();

        Category cat1 = createCategory("Giáo dục phòng ngừa", "Các bài viết giáo dục về phòng chống tệ nạn xã hội");
        Category cat2 = createCategory("Hỗ trợ gia đình", "Hướng dẫn và hỗ trợ các gia đình có người nghiện chất");

        createBlogPost(staff, cat1, "5 dấu hiệu nhận biết con em có nguy cơ sử dụng chất gây nghiện",
                "Là cha mẹ, việc nhận biết sớm...", "Một bài viết ngắn gọn về các dấu hiệu cảnh báo.");
        createBlogPost(consultant1, cat2, "Cách xây dựng môi trường gia đình tích cực phòng chống tệ nạn",
                "Gia đình là tuyến phòng thủ đầu tiên...", "Hướng dẫn chi tiết cho các bậc cha mẹ.");
        logger.info("Blog content seeded.");
    }

    private Category createCategory(String name, String description) {
        Category category = new Category(name, description);
        return categoryRepository.save(category);
    }

    private void createBlogPost(User author, Category category, String title, String content, String excerpt) {
        BlogPost blogPost = new BlogPost(author, category, title, content, excerpt);
        blogPostRepository.save(blogPost);
    }
}
