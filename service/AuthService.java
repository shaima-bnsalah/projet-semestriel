import com.gitquality.git_quality.btree.BTree;
import com.gitquality.git_quality.model.User;
import com.gitquality.git_quality.service.JwtService;

@Service
public class AuthService {

    private final BTree<String, User> userTree = new BTree<>();
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwtService;

    // ✅ Spring injecte BCryptPasswordEncoder depuis SecurityConfig
    public AuthService(JwtService jwtService, BCryptPasswordEncoder encoder) {
        this.jwtService = jwtService;
        this.encoder = encoder;
    }

    public String register(String username, String email, String password) {
        if (userTree.search(email) != null) {
            throw new RuntimeException("Email déjà utilisé !");
        }
        User user = new User(
            UUID.randomUUID().toString(),
            username,
            email,
            encoder.encode(password)
        );
        userTree.insert(email, user);
        return jwtService.generateToken(email);
    }

    public String login(String email, String password) {
        User user = userTree.search(email);
        if (user == null)
            throw new RuntimeException("Utilisateur non trouvé !");
        if (!encoder.matches(password, user.getPassword()))
            throw new RuntimeException("Mot de passe incorrect !");
        return jwtService.generateToken(email);
    }
}