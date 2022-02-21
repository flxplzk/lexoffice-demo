package de.nordakademie.iaa.examsurvey.persistence.impl;

import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.domain.User_;
import de.nordakademie.iaa.examsurvey.persistence.SpecificationExecutor;
import de.nordakademie.iaa.examsurvey.persistence.UserRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    private final SpecificationExecutor specificationExecutor;
    private final Class<User> entityClazz;

    @Autowired
    public UserRepositoryCustomImpl(final SpecificationExecutor specificationExecutor) {
        this.specificationExecutor = specificationExecutor;
        this.entityClazz = User.class;
    }

    @Override
    public Optional<User> findOneByUsername(final String userName) {
        return this.specificationExecutor.findOneBy(
                User_.byUsername(userName),
                this.entityClazz
        );
    }

}
