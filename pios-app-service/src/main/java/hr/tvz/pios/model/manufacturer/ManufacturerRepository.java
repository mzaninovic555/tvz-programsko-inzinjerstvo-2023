package hr.tvz.pios.model.manufacturer;

import hr.tvz.pios.model.post.Post;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ManufacturerRepository {

  Optional<Post> getById(Long id);

  void insert(Manufacturer manufacturer);

  Integer updateById(Manufacturer manufacturer);

  Integer deleteById(Long id);
}