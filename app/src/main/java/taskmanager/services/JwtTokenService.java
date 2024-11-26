package taskmanager.services;

import taskmanager.entities.JwtToken;
import taskmanager.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

public class JwtTokenService {

    public String getTokenForUser(int userId) {
        SessionFactory factory = HibernateUtil.getFactory();
        Session session = factory.openSession();

        try {
            Query<JwtToken> query = session.createQuery("FROM JwtToken WHERE user_id = :userId", JwtToken.class);
            query.setParameter("userId", userId);
            JwtToken jwtToken = query.uniqueResult();
            return jwtToken != null ? jwtToken.getJwtToken() : null;
        } finally {
            session.close();
        }
    }
}
