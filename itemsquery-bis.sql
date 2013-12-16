select items.*, pgabis.answer_number, qai.answer_order, -- pgabis.answer_order,
  pgabis.codpatient, pgabis.idpat, a.thevalue, ai.name, ai.idansitem,
  qai.pattern
  from (
    select it.item_order as itemord, it.content as content, q.codquestion as codq,
      it.iditem as itemid, it.highlight as highlight, it.ite_iditem as itparent,
      it."repeatable" as itrep, q.idquestion as idq,
      s.name as secname, s.idsection as secid
    from question q right join item it on (it.iditem = q.idquestion), section s,
    interview i
    where 1 = 1 -- it.idsection = 200
      and i.idinterview = 4651
      and i.idinterview = s.codinterview
      and s.section_order = 1
      and it.idsection = s.idsection
    ) items left join question_ansitem qai on (items.idq = qai.codquestion)
            left join answer_item ai on (qai.codansitem = ai.idansitem)

     left join (
      select *
      from pat_gives_answer2ques pga right join
            patient p on (pga.codpat = p.idpat)
      where p.codpatient = '57F093001'
    ) pgabis on (items.itemid = pgabis.codquestion and pgabis.answer_order = qai.answer_order)
   left join answer a on (pgabis.codanswer = a.idanswer)
order by itemord, answer_number, answer_order;


select items.*
from (
select it.item_order as itemord, it.content as content, q.codquestion as codq,
    it.iditem as itemid, it.highlight as highlight, it.ite_iditem as itparent,
    it."repeatable" as itrep, q.idquestion as idq,
    s.name as secname, s.idsection as secid
  from question q right join item it on (it.iditem = q.idquestion), section s,
  interview i
  where 1 = 1 -- it.idsection = 200
    and i.idinterview = 4651
    and i.idinterview = s.codinterview
    and s.section_order = 1
    and it.idsection = s.idsection
) items left join question_ansitem qai on (items.idq = qai.codquestion)
   left join answer_item ai on (qai.codansitem = ai.idansitem)
order by items.itemord; 

-- repeateable block items for a section
-- => items with a parent and the parent is repeatable
select it.item_order as itemord, it.content as content, q.codquestion as codq,
    it.iditem as itemid, it.highlight as highlight, it.ite_iditem as itparent,
    it."repeatable" as itrep, q.idquestion as idq,
    s.name as secname, s.idsection as secid
from question q right join item it on (it.iditem = q.idquestion), section s,
interview i
where 1 = 1 -- it.idsection = 200
  and i.idinterview = 50 -- 4651
  and i.idinterview = s.codinterview
  and s.section_order = 12
  and it.idsection = s.idsection
  and it.ite_iditem in (
    select ibis.iditem
    from item ibis
    where ibis."repeatable" is not null
  )
order by itemord;  








select *
    from pat_gives_answer2ques pga left join
          patient p on (pga.codpat = p.idpat)
    where p.codpatient = '57F093001'