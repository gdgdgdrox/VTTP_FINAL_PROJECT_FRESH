package com.deals.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailMessage;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import com.deals.server.model.Deal;
import com.deals.server.repository.UserRepository;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender; 

	@Autowired
	private UserRepository userRepo;

    public void sendEmail(List<String> recipients, List<Deal> newDeals){
        MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			for (String recipient : recipients){
				helper.addBcc(recipient);
			}
			helper.setSubject("New Deals");
			StringBuilder content = new StringBuilder();
			content.append("<html><body><h4>Dont miss out on these newly released Deals!</h4>");
			content.append("<table border='1'><tr><th>Title</th><th>Category</th><th>Website</th></tr>");
			for (Deal deal : newDeals) {
				content.append("<tr>");
				content.append("<td>").append(deal.getName()).append("</td>");
				content.append("<td>").append(deal.getCategory()).append("</td>");
                content.append("<td>").append(deal.getWebsiteURL()).append("</td>");
				content.append("</tr>");
			}
			content.append("</table>");
			content.append("<a href=\"www.deal4deal.com/user/unsubscribe\">tired of receiving this email? unsubscribe</a>");
			content.append("</body></html>");
			helper.setText(content.toString(), true);
			System.out.println("SENDING");
			mailSender.send(message);
		} catch (MessagingException e) {
			System.out.println("ERROR");
			e.printStackTrace();
		} 
    }

	public List<String> getSubscribersEmail(){
        return userRepo.getSubscribersEmail();
    }
}
