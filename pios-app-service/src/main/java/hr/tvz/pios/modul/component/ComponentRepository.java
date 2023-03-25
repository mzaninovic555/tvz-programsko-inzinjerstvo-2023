package hr.tvz.pios.modul.component;

import hr.tvz.pios.common.Type;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ComponentRepository {

  List<Component> getAll();

  Optional<Component> getById(Long id);

  Optional<Component> getByType(Type type);

  void insert(Component component);

  Integer updateById(Component component);

  Integer deleteById(Long id);
}
