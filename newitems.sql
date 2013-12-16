/*
select items.*, pgabis.answer_number, pgabis.answer_order, p.codpatient,
	p.idpat, a.thevalue, ai.name
  from (
    select it.item_order as itemord, it.content as content, q.codquestion as codq,
      it.iditem as itemid, it.highlight as highlight, it.ite_iditem as itparent,
      it."repeatable" as itrep, q.idquestion as idq,
      s.name as secname, s.idsection as secid
    from question q right join item it on (it.iditem = q.idquestion), section s
    where it.idsection = 200
      and it.idsection = s.idsection
    ) items left join (
      select *
      from pat_gives_answer2ques pga
      where pga.codpat = 3060 -- 3060 -> 75 rows || 350 -> 72 rows
    ) pgabis on (items.itemid = pgabis.codquestion)
	  left join answer a on (pgabis.codanswer = a.idanswer)
	  left join question_ansitem qai on (pgabis.codquestion = qai.codquestion and pgabis.answer_order = qai.answer_order)
	  left join answer_item ai on (qai.codansitem = ai.idansitem)
	  left join patient p on (pgabis.codpat = p.idpat)
order by itemord, answer_number, answer_order;
*/


select items.*, pgabis.answer_number, pgabis.answer_order, pgabis.codpatient, pgabis.idpat, a.thevalue,
		ai.name, ai.idansitem, qai.pattern
  from (
    select it.item_order as itemord, it.content as content, q.codquestion as codq,
      it.iditem as itemid, it.highlight as highlight, it.ite_iditem as itparent,
      it."repeatable" as itrep, q.idquestion as idq,
      s.name as secname, s.idsection as secid
    from question q right join item it on (it.iditem = q.idquestion), section s,
  	interview i
    where 1 = 1 -- it.idsection = 200
    	and i.idinterview = 50
      and i.idinterview = s.codinterview
      and s.section_order = 3
      and it.idsection = s.idsection
    ) items left join (
      select *
      from pat_gives_answer2ques pga, patient p
-- 2993 (157011063); 3060 (157081003) -> 75 rows || 350 (157011024) -> 72 rows
      where pga.codpat = p.idpat
      	and p.codpatient = '157081003'
    ) pgabis on (items.itemid = pgabis.codquestion)
	  left join answer a on (pgabis.codanswer = a.idanswer)
	  left join question_ansitem qai on (pgabis.codquestion = qai.codquestion and pgabis.answer_order = qai.answer_order)
	  left join answer_item ai on (qai.codansitem = ai.idansitem)
--	  left join patient p on (pgabis.codpat = p.idpat)
order by itemord, answer_number, answer_order;