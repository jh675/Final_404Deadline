package com.example.demo.project.calender.mapper;

<<<<<<< HEAD
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CalenderMapper {


}
=======
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import com.example.demo.project.calender.CalenderVO;

@Mapper
public interface CalenderMapper {
 List<CalenderVO> selectAll(CalenderVO vo);
 CalenderVO selectOne(int id);
 int insert(CalenderVO vo);
 int updete(CalenderVO vo);
 int delete(int id);
 
}
>>>>>>> 5b2578b0144c8d3eb5a483aba11be9bcfdeb31b8
