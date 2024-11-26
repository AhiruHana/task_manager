package taskmanager.controllers;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import taskmanager.entities.Task;
import taskmanager.entities.Board;
import taskmanager.entities.Col;
import taskmanager.entities.TaskAssignment;
import taskmanager.entities.User;
import taskmanager.entities.Workspace;

import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {
    private final static SessionFactory FACTORY;

    static {
        Configuration conf = new Configuration();
        conf.configure("hibernate.cfg.xml");
        conf.addAnnotatedClass(Board.class);
        conf.addAnnotatedClass(Col.class);
        conf.addAnnotatedClass(Task.class);
        conf.addAnnotatedClass(TaskAssignment.class);
        conf.addAnnotatedClass(User.class);
        conf.addAnnotatedClass(Workspace.class);

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
        .applySettings(conf.getProperties())
        .build();
        FACTORY = conf.buildSessionFactory(registry);
    }

    public static SessionFactory getFactory() {
        return FACTORY;
    }
}
