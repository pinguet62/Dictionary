package fr.pinguet62.jsfring.dao.nosql;

import fr.pinguet62.jsfring.model.nosql.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * @see User
 */
@Repository("nosql.userDao")
public interface UserDao extends MongoRepository<User, ObjectId>, QuerydslPredicateExecutor<User> {

    User findByPseudo(String pseudo);

}