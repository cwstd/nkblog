package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;
    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void updateUser() {
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "http://www.nowcoder.com/102.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "hello");
        System.out.println(rows);
    }

    @Test
    public void testSelectPosts() {
//        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0, 0, 10);
//        for(DiscussPost post : list) {
//            System.out.println(post);
//        }
//
//        int rows = discussPostMapper.selectDiscussPostRows(0);
//        System.out.println(rows);
    }
    @Test public void login_ticket_Test(){
        loginTicketMapper.insertLoginTicket(new LoginTicket(101,"abc",0,new Date(System.currentTimeMillis())));
        LoginTicket abc = loginTicketMapper.selectLoginTicket("abc");
        System.out.println(abc);
        loginTicketMapper.updateStatus("abc",1);
        LoginTicket abc1 = loginTicketMapper.selectLoginTicket("abc");
        System.out.println(abc1);
    }

    @Test
    public void insertdiscusspost(){
        discussPostMapper.insertDiscussPost(new DiscussPost(101,"真好玩","今年的就业形势，确实不容乐观",0,0,new Date(),0,10));
    }
    @Test
    public void MessageMapperTest(){
        List<Message> messages = messageMapper.selectConversation(111, 0, 20);
        System.out.println(messages.toString());
        int i = messageMapper.selectConversationCount(111);
        System.out.println(i);
        List<Message> messages1 = messageMapper.selectLetters("111-112", 0, 20);
        System.out.println(messages1);
    }

}
