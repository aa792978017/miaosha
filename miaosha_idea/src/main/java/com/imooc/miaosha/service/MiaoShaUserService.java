package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.MiaoShaUserDao;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.exception.GlobalException;
import com.imooc.miaosha.redis.MiaoShaUserKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoShaUserService {

    //定义token常量
    public static final String COOKI_NAME_TOKEN = "token";

    @Autowired
    private RedisService redisService;


    @Autowired
    private MiaoShaUserDao miaoShaUserDao;

    public MiaoshaUser getById(long id){
        //取缓存
        MiaoshaUser user = redisService.get(MiaoShaUserKey.getById,""+id, MiaoshaUser.class);
        if (user != null){
            return user;
        }
        //取数据库
        user = miaoShaUserDao.getById(id);
        if (user != null){
            //放入缓存
            redisService.set(MiaoShaUserKey.getById,""+id, user);
        }
        return user;
    }

    public boolean updatePassword(String token, long id, String formPass){
       MiaoshaUser user = getById(id);  //取用户
       if (user == null){
           throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);  //数据库和缓存都没有,说明用户不存在
       }
       //更新数据库
       MiaoshaUser toBeUpdate = new MiaoshaUser(); //这样只修改变化的地方,减少bin.log
       toBeUpdate.setId(id);
       toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass,user.getSalt()));
       miaoShaUserDao.update(toBeUpdate);
       //处理缓存
       redisService.del(MiaoShaUserKey.getById, ""+id);
       user.setPassword(toBeUpdate.getPassword());
       redisService.set(MiaoShaUserKey.token, token,user);
       return true;



    }

    /**
     * 进行登录检验:通过数据库检索判断账号密码是否正确
     * @param response
     * @param loginVo
     * @return
     */
    public String login(HttpServletResponse response, LoginVo loginVo){
        if(loginVo == null){  //账号信息为空
            throw new GlobalException( CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        MiaoshaUser user = miaoShaUserDao.getById(Long.valueOf(mobile));
        if (user == null){ //账号不存在
            throw new GlobalException( CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        String saltDb = user.getSalt();
        //第二次MD5加密,判断密码是否正确
        String calcPss = MD5Util.formPassToDBPass(formPass,saltDb);
        if (!calcPss.equals(dbPass)){  //密码错误
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        // 生成Cookie,缓存账号token到redis
        String token = UUIDUtil.uuid();
        addCookie(response, token, user);
        return token;


    }

    public MiaoshaUser getByToken(HttpServletResponse response,
                                  String token) {
        if(StringUtils.isEmpty(token)){
            return null;
        }
        //缓存账号token到redis,       //对象级缓存,粒度是最小的
        MiaoshaUser user = redisService.get(MiaoShaUserKey.token,token, MiaoshaUser.class);
        //延长session有效期
        if (user != null){
            addCookie(response, token ,user);
        }
        return user;


    }

    private void addCookie(HttpServletResponse response,String token, MiaoshaUser user){
        redisService.set(MiaoShaUserKey.token,token,user);
        Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoShaUserKey.token.getExpireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
