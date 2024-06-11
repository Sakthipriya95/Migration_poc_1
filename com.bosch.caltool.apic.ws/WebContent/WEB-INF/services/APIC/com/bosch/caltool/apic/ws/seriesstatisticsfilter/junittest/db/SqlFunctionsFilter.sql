select *
  from ( select func.name functionname
		      , val.label
		      , val.CONTENT.getValue() cont
		      , count(val.CONTENT.getValue()) count_for_peak
		   from ta2l_functions func
		      , ta2l_modules   mod       
		      , tabv_caldatafiles cdf   
		      , TABV_CALDATAVALUES2FILES val2fil
		      , TABV_CALDATAVALUES val
		  where func.module_id = mod.module_id
		    and mod.file_id = cdf.a2l_info_id     
		    and val2fil.FILE_ID = cdf.ID
		    and val.ID = val2fil.VALUE_ID
		    and func.name = ?
		    and func.functionversion like ?
		    and val.LABEL = ?
		  group by func.name
		         , val.CONTENT.getValue()
		         , val.label
		  order by count_for_peak desc
		) where rownum = 1