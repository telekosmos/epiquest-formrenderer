
select p.codpatient, a.idanswer, a.thevalue, q.codquestion as codques, i.content
from patient p, pat_gives_answer2ques pga, answer a, question q, item i
where p.codhosp in ('01','02','03') 
  and p.idpat = pga.codpat
  and a.idanswer = pga.codanswer
  and q.idquestion = pga.codquestion
  and q.codquestion in ('A1', 'A1A')
  and i.iditem = q.idquestion
order by 1, 4;



select pga.codquestion, pga.codanswer, pga.answer_order, pga.answer_number
from pat_gives_answer2ques pga
where pga.codpat = 1100 -- 350
and pga.codquestion in (
select q.idquestion
from interview i, section s, item it, question q
where i.idinterview = 50
  and s.codinterview = i.idinterview
  and it.idsection = s.idsection
  and it.iditem = q.idquestion
)
order by 1, 4, 3;


select p.idpat as patid, p.codpatient as patientcod, count (q.codquestion) as numcodq
from patient p, pat_gives_answer2ques pga, appgroup g, 
performance pf, 
		question q, answer a, interview i, item it, section s, project pj
where 1 = 1
	and g.idgroup = 304
  and pf.codgroup = g.idgroup
  and i.idinterview = 50
  and pj.project_code = '157'
  and pj.idprj = i.codprj
  and pf.codinterview = i.idinterview
  and s.codinterview = i.idinterview
  and pf.codpat = p.idpat
  
  and pga.codpat = p.idpat
  and pga.codquestion = q.idquestion
  and pga.codanswer = a.idanswer
  and q.idquestion = it.iditem
  and it.idsection = s.idsection
  and s.section_order = 12
  and p.codpatient <> '15700000000'
  and p.codpatient <> '1570110009'
  and p.codpatient <> '15769696969'
group by p.codpatient, p.idpat
order by 3 desc, 2;



-- all questions for a interview (50) and a patient (157011038)
SELECT s.name, q.idquestion, q.codquestion as codq, pga.answer_number as ansnum, 
	pga.answer_order as ansord, a.thevalue as val, s.section_order, it.item_order
from interview i, section s, patient p, item it, question q,
		performance pf, pat_gives_answer2ques pga, answer a
where i.idinterview = 50
  and s.codinterview = i.idinterview
  and it.idsection = s.idsection
  and q.idquestion = it.iditem
  and p.codpatient = '157011038'
  and pf.codinterview = i.idinterview
  and pf.codpat = p.idpat
  and pga.codquestion = q.idquestion
  and pga.codpat = p.idpat
  and pga.codanswer = a.idanswer
-- order by 5, 2, 6, 3;
order by 7, 4, 8, 5;



-- questions-answers for a interview (50) patient and section (9)
select p.codpatient, g.name as grpname, i.name as intrvname, s.name as secname, 
q.codquestion as codq, a.thevalue, s.section_order, it.item_order, 
pga.answer_order, pga.answer_number, it."repeatable" as itrep,
ai.name, ai.idansitem, p.idpat, s.idsection
from patient p, pat_gives_answer2ques pga, appgroup g, performance pf, 
question q, answer a, interview i, item it, section s, project pj,
 question_ansitem qa, answer_item ai 
where 1=1  
	and i.idinterview = 50 
  and pj.project_code = '157' 
  and pj.idprj = i.codprj 
  and pf.codinterview = i.idinterview 
  and pf.codgroup = g.idgroup 
  and s.codinterview = i.idinterview 
  and pf.codpat = p.idpat 
  and pga.codpat = p.idpat 
  and p.codpatient = '157081003' -- '157011001'
  and pga.codquestion = q.idquestion 
  and pga.codanswer = a.idanswer 
  
  and q.idquestion = qa.codquestion
  and qa.codansitem = ai.idansitem
  and a.codansitem = ai.idansitem
  and ai.codintrv = i.idinterview
  
  and q.idquestion = it.iditem 
  and it.idsection = s.idsection 
  and s.section_order = 2
order by 1, 7, 10, 8, 5, 9;





