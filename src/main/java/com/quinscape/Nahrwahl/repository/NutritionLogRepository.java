package com.quinscape.Nahrwahl.repository;

import com.quinscape.Nahrwahl.model.nutritionLog.NutritionLog;
import java.util.Date;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutritionLogRepository extends MongoRepository<NutritionLog, String> {

  Optional<NutritionLog> findByDateAndUserId(Date date, String userId);
  
}
