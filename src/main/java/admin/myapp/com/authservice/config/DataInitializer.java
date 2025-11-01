package admin.myapp.com.authservice.config;//package admin.myapp.com.authservice.config;
//
//import admin.myapp.com.authservice.constant.Constants;
//import admin.myapp.com.authservice.entity.Role;
//import admin.myapp.com.authservice.repositoy.RoleRepo;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//
///**
// * Initializes default roles in the database upon application startup.
// * <p>
// * This component checks if any roles exist in the database, and if none are found,
// * it inserts default roles such as "ADMIN" and "USER".
// * </p>
// * <p>
// * This ensures that the system has essential roles available for user management and authorization.
// * </p>
// */
//@Component
//@RequiredArgsConstructor
//public class DataInitializer {
//
//    private final RoleRepo roleRepository;
//
//
//    /**
//     * Inserts default roles ("ADMIN" and "USER") into the database if none exist.
//     * <p>
//     * This method is executed once after the bean's construction thanks to the {@link PostConstruct} annotation.
//     * </p>
//     * <p>
//     * If roles are already present, seeding is skipped to avoid duplication.
//     * </p>
//     */
//    @PostConstruct
//    public void insertDefaultRoles() {
//        if (roleRepository.count() == 0) {
//            roleRepository.save(new Role(Constants.ADMIN));
//            roleRepository.save(new Role(Constants.USER));
//            System.out.println("✅ Default roles seeded to database");
//        } else {
//            System.out.println("ℹ️ Roles already exist — skipping seeding");
//        }
//    }
//}
