package mil.dds.anet.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import mil.dds.anet.AnetObjectEngine;
import mil.dds.anet.beans.AdvisorOrganization;
import mil.dds.anet.beans.ApprovalAction;
import mil.dds.anet.beans.ApprovalAction.ApprovalType;
import mil.dds.anet.beans.ApprovalStep;
import mil.dds.anet.beans.Person;
import mil.dds.anet.beans.Report;
import mil.dds.anet.beans.Report.ReportState;
import mil.dds.anet.database.ReportDao;
import mil.dds.anet.utils.ResponseUtils;

@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
public class ReportResource {

	ReportDao dao;
	AnetObjectEngine engine;
	
	public ReportResource(AnetObjectEngine engine) {
		this.engine = engine;
		this.dao = engine.getReportDao();
	}
	
	@GET
	@Path("/{id}")
	public Report getById(@PathParam("id") int id) { 
		return dao.getById(id);
	}
	
	@POST
	@Path("/new")
	public Report createNewReport(Report r) { 
		if (r.getState() == null) { r.setState(ReportState.DRAFT); }
		//TODO: set the author to the person who submits this HTTP request. 
		int id = dao.createNewReport(r);
		r.setId(id);
		return r;
	}
	
	
	/* Submit a report for approval
	 * Kicks a report from DRAFT to PENDING_APPROVAL and sets the approval step Id
	 */
	@GET
	@Path("/{id}/submit")
	public Response submitReport(@PathParam("id") int id) { 
		Report r = dao.getById(id);
		
		AdvisorOrganization ao = engine.getAdvisorOrganizationForPerson(r.getAuthor());
		if (ao == null ) { 
			return ResponseUtils.withMsg("Unable to find AO for Report Author", Status.BAD_REQUEST);
		}
		List<ApprovalStep> steps = engine.getApprovalStepsForOrg(ao);
		if (steps == null || steps.size() == 0) {  
			return ResponseUtils.withMsg("Unable to find approval steps for AO", Status.BAD_REQUEST);
		}
		
		//Push the report into the first step of this workflow
		r.setApprovalStepId(steps.get(0).getId());
		r.setState(ReportState.PENDING_APPROVAL);
		int numRows = dao.updateReport(r);
		return (numRows == 1) ? Response.ok().build() : ResponseUtils.withMsg("No records updated", Status.BAD_REQUEST); 
	}
	
	/*
	 * Approve this report for the current step. 
	 * TODO: this should run common approval code that checks if any previous approving users can approve the future steps
	 */
	@GET
	@Path("/{id}/approve")
	public Response approveReport(@PathParam("id") int id) { 
		Report r = dao.getById(id);
		ApprovalStep step = engine.getApprovalStepDao().getById(r.getApprovalStepId());
		
		//Verify that this user can approve for this step. 
		int userId = 4;
		//TODO: know who this user is! 
//		boolean canApprove = engine.canUserApproveStep(userId, step.getId());
		boolean canApprove = true;
		if (canApprove == false) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		//Write the approval
		//TODO: this should be in a transaction.... 
		ApprovalAction approval = new ApprovalAction();
		approval.setReport(r);
		approval.setStep(ApprovalStep.create(r.getApprovalStepId(), null, null, null));
		approval.setPerson(Person.createWithId(userId));
		approval.setType(ApprovalType.APPROVE);
		engine.getApprovalActionDao().createAction(approval);
		
		//Update the report
		r.setApprovalStepId(step.getNextStepId());
		if (step.getNextStepId() == null) {
			r.setState(ReportState.RELEASED);
		}
		int numRows = dao.updateReport(r);
		//TODO: close the transaction. 
		
		return Response.ok().build();
	}
	
}
