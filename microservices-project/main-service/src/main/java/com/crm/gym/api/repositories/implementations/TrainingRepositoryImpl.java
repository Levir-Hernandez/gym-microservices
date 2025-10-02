package com.crm.gym.api.repositories.implementations;

import com.crm.gym.api.entities.Trainee;
import com.crm.gym.api.entities.Trainer;
import com.crm.gym.api.entities.Training;
import com.crm.gym.api.entities.TrainingType;
import com.crm.gym.api.repositories.TrainingQueryCriteria;
import com.crm.gym.api.repositories.interfaces.DynamicQueryRepository;
import com.crm.gym.api.repositories.interfaces.Identifiable;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.NoResultException;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.time.LocalDate;
import java.util.function.*;

@Repository
public class TrainingRepositoryImpl
        extends TemplateRepositoryImpl<UUID, Training>
        implements DynamicQueryRepository
{
    @Override
    protected Class<Training> getEntityClass() {return Training.class;}

    @Override
    public Training create(Training training)
    {
        resolveReferencesByAltKeys(training);
        nullifyInvalidReferences(training);
        return super.create(training);
    }

    @Override
    public Training update(UUID entityId, Training training)
    {
        resolveReferencesByAltKeys(training);
        nullifyInvalidReferences(training);
        return super.update(entityId, training);
    }

    @Override
    public List<Training> findByCriteria(TrainingQueryCriteria criteria)
    {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        return findByCriteriaQuery(cb, criteria).getResultList();
    }

    @Override
    public Page<Training> findByCriteria(TrainingQueryCriteria criteria, Pageable pageable)
    {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        TypedQuery<Training> pagedFindByCriteriaQuery = findByCriteriaQuery(cb, criteria)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        TypedQuery<Long> countByCriteriaQuery = buildQueryTemplateByCriteria(
                cb, criteria,
                Long.class,
                trainingRoot -> cb.count(trainingRoot) // Counts Trainings matching criteria
        );

        Long total = countByCriteriaQuery.getSingleResult();
        List<Training> content = pagedFindByCriteriaQuery.getResultList();

        return new PageImpl<>(content, pageable, total);
    }

    private TypedQuery<Training> findByCriteriaQuery(CriteriaBuilder cb, TrainingQueryCriteria criteria)
    {
        // Query to fetch Trainings matching the given criteria
        return buildQueryTemplateByCriteria(
                cb, criteria,
                Training.class,
                trainingRoot -> trainingRoot // Selects the entire Training entity
        );
    }

    private <T> TypedQuery<T> buildQueryTemplateByCriteria(
            CriteriaBuilder cb, TrainingQueryCriteria criteria,
            Class<T> resultClass, Function<Root<Training>, Selection<? extends T>> trainingSelection
    )
    {
        CriteriaQuery<T> cq = cb.createQuery(resultClass);
        Root<Training> training = cq.from(Training.class);
        List<Predicate> predicates = buildTrainingPredicates(cb, training, criteria);
        cq.select(trainingSelection.apply(training)).where(cb.and(predicates.toArray(Predicate[]::new)));
        return em.createQuery(cq);
    }

    private List<Predicate> buildTrainingPredicates(CriteriaBuilder cb, Root<Training> training, TrainingQueryCriteria criteria)
    {
        List<Predicate> predicates = new ArrayList<>();

        BiConsumer<Object, Function<Object, Predicate>> addPredicate =
                (field, predicateMapper) ->
                        Optional.ofNullable(field)
                                .ifPresent(f -> predicates.add(predicateMapper.apply(f)));

        addPredicate.accept(
                criteria.getTraineeUsername(),
                traineeUsername -> cb.equal(training.get("trainee").get("username"), traineeUsername)
        );

        addPredicate.accept(
                criteria.getTrainerUsername(),
                trainerUsername -> cb.equal(training.get("trainer").get("username"), trainerUsername)
        );

        addPredicate.accept(
                criteria.getFromDate(),
                fromDate -> cb.greaterThanOrEqualTo(training.get("date"), (LocalDate) fromDate)
        );

        addPredicate.accept(
                criteria.getToDate(),
                toDate -> cb.lessThanOrEqualTo(training.get("date"), (LocalDate) toDate)
        );

        addPredicate.accept(
                criteria.getTrainingTypeName(),
                trainingTypeName -> cb.equal(training.get("trainingType").get("name"), trainingTypeName)
        );

        return predicates;
    }

    private void nullifyInvalidReferences(Training training)
    {
        BiPredicate<Identifiable<?>, Class<? extends Identifiable<?>>> isInvalidReference;

        isInvalidReference = (entity, entityClass) -> Optional
                .ofNullable(entity)
                .map(Identifiable::getId)
                .map(id -> em.find(entityClass, id))
                .isEmpty();

        boolean invalidTrainingType = isInvalidReference.test(training.getTrainingType(), TrainingType.class);
        boolean invalidTrainee = isInvalidReference.test(training.getTrainee(), Trainee.class);
        boolean invalidTrainer = isInvalidReference.test(training.getTrainer(), Trainer.class);

        if(invalidTrainingType) {training.setTrainingType(null);}
        if(invalidTrainee) {training.setTrainee(null);}
        if(invalidTrainer) {training.setTrainer(null);}
    }

    private void resolveReferencesByAltKeys(Training training)
    {
        // resolveTrainingTypeReferenceByAltKeys
        resolveTemplateReferenceByAltKeys(
                "name", () -> training.getTrainingType().getName(),
                training::getTrainingType, training::setTrainingType
        );

        // resolveTraineeReferenceByAltKeys
        resolveTemplateReferenceByAltKeys(
                "username", () -> training.getTrainee().getUsername(),
                training::getTrainee, training::setTrainee
        );

        // resolveTrainerReferenceByAltKeys
        resolveTemplateReferenceByAltKeys(
                "username", () -> training.getTrainer().getUsername(),
                training::getTrainer, training::setTrainer
        );
    }

    private <T extends Identifiable<?>> void resolveTemplateReferenceByAltKeys(String fieldName, Supplier<String> fieldValueSupplier,
                                                                               Supplier<T> fieldGetter, Consumer<T> fieldSetter)
    {
        T reference = fieldGetter.get();
        if(Objects.isNull(reference) || Objects.nonNull(reference.getId()) || Objects.isNull(fieldValueSupplier.get())) {return;}
        try
        {
            reference = (T) em.createQuery("SELECT t FROM "+reference.getClass().getSimpleName()+" t WHERE t."+fieldName+" = :"+fieldName)
                    .setParameter(fieldName, fieldValueSupplier.get())
                    .getSingleResult();
        }
        catch (NoResultException e) {reference = null;}
        fieldSetter.accept(reference);
    }
}
