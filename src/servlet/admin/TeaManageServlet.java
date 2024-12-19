package servlet.admin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import model.Tea;
import model.Catalog;
import model.PageBean;
import model.UpLoadImg;
import dao.TeaDao;
import dao.CatalogDao;
import dao.UpLoadImgDao;
import dao.impl.TeaDaoImpl;
import dao.impl.CatalogDaoImpl;
import dao.impl.UpLoadImgDaoImpl;
import utils.RanUtil;

import net.sf.json.JSONObject;

/**
 * 商品管理Servlet
 * Created time: 2024-12-18
 * @author null7777777
 */
@WebServlet("/jsp/admin/TeaManageServlet")
public class TeaManageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String TEALIST_PATH = "teaManage/teaList.jsp";
    private static final String TEAADD_PATH = "teaManage/teaAdd.jsp";
    private static final String TEAEDIT_PATH = "teaManage/teaEdit.jsp";
    private static final String TEADETAIL_PATH = "teaManage/teaDetail.jsp";
    private static final String TEAIMGDIR_PATH = "images/tea/teaimg/";
    
    // 文件上传配置
    private static final int MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB 
    private static final String[] ALLOWED_CONTENT_TYPES = {"image/jpeg", "image/png"};

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            teaList(request, response);
            return;
        }

        switch(action) {
            case "list":
                teaList(request, response);
                break;
            case "detail":
                teaDetail(request, response);
                break;
            case "addReq":
                teaAddReq(request, response);
                break;
            case "add":
                teaAdd(request, response);
                break;
            case "edit":
                teaEdit(request, response);
                break;
            case "update":
                teaUpdate(request, response);
                break;
            case "find":
                teaFind(request, response);
                break;
            case "updateImg":
                updateImg(request, response);
                break;
            case "del":
                teaDel(request, response);
                break;
            case "search":
                searchTea(request, response);
                break;
            default:
                teaList(request, response);
        }
    }
    
 // 获取商品列表（分页）
    private void teaList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        int curPage = 1;
        String page = request.getParameter("page");
        if (page != null) {
            try {
                curPage = Integer.parseInt(page);
            } catch (NumberFormatException e) {
                // 解析失败使用默认值1
            }
        }
        int maxSize = Integer.parseInt(request.getServletContext().getInitParameter("maxPageSize"));
        TeaDao teaDao = new TeaDaoImpl();
        PageBean pb = new PageBean(curPage, maxSize, teaDao.teaReadCount());
        
        request.setAttribute("pageBean", pb);
        request.setAttribute("teaList", teaDao.teaList(pb));
        request.getRequestDispatcher(TEALIST_PATH).forward(request, response);
    }

    // 商品详情
    private void teaDetail(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if(idStr != null && !idStr.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                TeaDao teaDao = new TeaDaoImpl();
                Tea tea = teaDao.findTeaById(id);
                if(tea != null) {
                    request.setAttribute("teaInfo", tea);
                    request.getRequestDispatcher(TEADETAIL_PATH).forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                // ID格式错误
            }
        }
        // 如果出现任何错误，重定向到列表页
        request.setAttribute("teaMessage", "商品不存在");
        teaList(request, response);
    }

    // 准备添加商品
    private void teaAddReq(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        CatalogDao catalogDao = new CatalogDaoImpl();
        List<Catalog> catalogs = catalogDao.getCatalog();
        request.setAttribute("catalog", catalogs);
        request.getRequestDispatcher(TEAADD_PATH).forward(request, response);
    }

    // 添加商品
    private void teaAdd(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // 验证是否是多部分请求
            if (!ServletFileUpload.isMultipartContent(request)) {
                request.setAttribute("teaMessage", "错误的表单类型");
                teaAddReq(request, response);
                return;
            }

            // 准备文件上传
            File contextPath = new File(request.getServletContext().getRealPath("/"));
            File dirPath = new File(contextPath, TEAIMGDIR_PATH);
            if (!dirPath.exists()) {
                dirPath.mkdirs();
            }

            // 创建文件上传工厂
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(MAX_FILE_SIZE);
            
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(MAX_FILE_SIZE);
            upload.setHeaderEncoding("UTF-8");

            // 解析请求
            List<FileItem> items = upload.parseRequest(request);
            Tea tea = new Tea();
            UpLoadImg upImg = new UpLoadImg();
            boolean hasImage = false;

            // 处理表单项
            for (FileItem item : items) {
                if (item.isFormField()) {
                    processFormField(item, tea);
                } else {
                    if (item.getSize() > 0) {
                        hasImage = processUploadedFile(item, upImg, dirPath, contextPath);
                    }
                }
            }

            // 验证必填字段
            if (tea.getTeaName() == null || tea.getPrice() <= 0 || !hasImage) {
                request.setAttribute("teaMessage", "请填写完整的商品信息");
                teaAddReq(request, response);
                return;
            }

            // 保存数据到数据库
            UpLoadImgDao upImgDao = new UpLoadImgDaoImpl();
            if (upImgDao.imgAdd(upImg)) {
                UpLoadImg savedImg = upImgDao.findLastImg();
                if (savedImg != null) {
                    tea.setImgId(savedImg.getImgId());
                    tea.setAddTime(new Date());

                    TeaDao teaDao = new TeaDaoImpl();
                    if (teaDao.teaAdd(tea)) {
                        request.setAttribute("teaMessage", "商品添加成功");
                        teaList(request, response);
                        return;
                    }
                }
            }

            // 添加失败，清理已上传的图片
            File uploadedFile = new File(contextPath, upImg.getImgSrc());
            if (uploadedFile.exists()) {
                uploadedFile.delete();
            }
            request.setAttribute("teaMessage", "商品添加失败");
            teaAddReq(request, response);

        } catch (Exception e) {
            request.setAttribute("teaMessage", "系统错误: " + e.getMessage());
            teaAddReq(request, response);
        }
    }
    
 // 处理表单字段
    private void processFormField(FileItem item, Tea tea) throws Exception {
        String fieldName = item.getFieldName();
        String value = item.getString("UTF-8");
        
        switch (fieldName) {
            case "teaName":
                tea.setTeaName(value.trim());
                break;
            case "price":
                if (!value.isEmpty()) {
                    tea.setPrice(Double.parseDouble(value));
                }
                break;
            case "description":
                tea.setDescription(value.trim());
                break;
            case "catalog":
                if (!value.isEmpty()) {
                    tea.setCatalogId(Integer.parseInt(value));
                }
                break;
            case "recommend":
                tea.setRecommend(Boolean.parseBoolean(value));
                break;
        }
    }

    // 处理上传的文件
    private boolean processUploadedFile(FileItem item, UpLoadImg upImg, File dirPath, File contextPath) 
            throws Exception {
        String contentType = item.getContentType();
        boolean isAllowedType = false;
        for (String allowedType : ALLOWED_CONTENT_TYPES) {
            if (allowedType.equals(contentType)) {
                isAllowedType = true;
                break;
            }
        }
        
        if (!isAllowedType) {
            throw new IllegalArgumentException("不支持的文件类型");
        }

        String extension = contentType.equals("image/jpeg") ? ".jpg" : ".png";
        String imgName = RanUtil.getUUID() + extension;
        String imgSrc = TEAIMGDIR_PATH + imgName;
        
        File imgFile = new File(dirPath, imgName);
        try (InputStream in = item.getInputStream();
             OutputStream out = new FileOutputStream(imgFile)) {
            IOUtils.copy(in, out);
        }
        
        upImg.setImgName(imgName);
        upImg.setImgSrc(imgSrc);
        upImg.setImgType(contentType);
        
        return true;
    }

    // 编辑商品页面
    private void teaEdit(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int teaId = Integer.parseInt(idStr);
                TeaDao teaDao = new TeaDaoImpl();
                CatalogDao catalogDao = new CatalogDaoImpl();
                
                Tea tea = teaDao.findTeaById(teaId);
                List<Catalog> catalogs = catalogDao.getCatalog();
                
                if (tea != null && catalogs != null) {
                    request.setAttribute("teaInfo", tea);
                    request.setAttribute("catalog", catalogs);
                    request.getRequestDispatcher(TEAEDIT_PATH).forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                // ID格式错误
            }
        }
        request.setAttribute("teaMessage", "商品不存在");
        teaList(request, response);
    }

    // 更新商品信息
    private void teaUpdate(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        Tea tea = new Tea();
        
        // 获取并验证必要参数
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            request.setAttribute("teaMessage", "商品ID不能为空");
            teaList(request, response);
            return;
        }

        try {
            tea.setTeaId(Integer.parseInt(idStr));
            
            // 设置商品名称
            String teaName = request.getParameter("teaName");
            if (teaName != null && !teaName.trim().isEmpty()) {
                tea.setTeaName(teaName.trim());
            }
            
            // 设置商品价格
            String priceStr = request.getParameter("price");
            if (priceStr != null && !priceStr.trim().isEmpty()) {
                tea.setPrice(Double.parseDouble(priceStr));
            }
            
            // 设置商品分类
            String catalogIdStr = request.getParameter("catalog");
            if (catalogIdStr != null && !catalogIdStr.trim().isEmpty()) {
                tea.setCatalogId(Integer.parseInt(catalogIdStr));
            }
            
            // 设置商品描述
            String description = request.getParameter("description");
            if (description != null) {
                tea.setDescription(description.trim());
            }
            
            // 设置推荐状态
            String recommendStr = request.getParameter("recommend");
            tea.setRecommend(recommendStr != null && "true".equals(recommendStr));
            
            // 执行更新
            TeaDao teaDao = new TeaDaoImpl();
            if (teaDao.teaUpdate(tea)) {
                request.setAttribute("teaMessage", "商品更新成功");
                teaList(request, response);
            } else {
                request.setAttribute("teaMessage", "商品更新失败");
                request.setAttribute("teaInfo", teaDao.findTeaById(tea.getTeaId()));
                request.getRequestDispatcher(TEAEDIT_PATH).forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("teaMessage", "参数格式错误");
            teaList(request, response);
        }
    }
    
 // 更新商品图片
    private void updateImg(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            request.setAttribute("teaMessage", "商品ID不能为空");
            teaList(request, response);
            return;
        }

        try {
            int teaId = Integer.parseInt(idStr);
            TeaDao teaDao = new TeaDaoImpl();
            UpLoadImgDao upImgDao = new UpLoadImgDaoImpl();
            
            // 获取原始商品信息
            Tea tea = teaDao.findTeaById(teaId);
            if (tea == null) {
                request.setAttribute("teaMessage", "商品不存在");
                teaList(request, response);
                return;
            }

            // 处理文件上传
            File contextPath = new File(request.getServletContext().getRealPath("/"));
            File dirPath = new File(contextPath, TEAIMGDIR_PATH);
            if (!dirPath.exists()) {
                dirPath.mkdirs();
            }

            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(MAX_FILE_SIZE);
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(MAX_FILE_SIZE);

            List<FileItem> items = upload.parseRequest(request);
            for (FileItem item : items) {
                if (!item.isFormField() && item.getSize() > 0) {
                    UpLoadImg upImg = tea.getUpLoadImg();
                    if (processUploadedFile(item, upImg, dirPath, contextPath)) {
                        // 删除旧图片
                        File oldImg = new File(contextPath, tea.getUpLoadImg().getImgSrc());
                        if (oldImg.exists()) {
                            oldImg.delete();
                        }
                        
                        // 更新图片信息
                        if (upImgDao.imgUpdate(upImg)) {
                            request.setAttribute("teaMessage", "图片更新成功");
                        } else {
                            request.setAttribute("teaMessage", "图片更新失败");
                        }
                    }
                    break;
                }
            }

        } catch (Exception e) {
            request.setAttribute("teaMessage", "图片更新失败: " + e.getMessage());
        }
        
        teaEdit(request, response);
    }

    // 删除商品
    private void teaDel(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                TeaDao teaDao = new TeaDaoImpl();
                UpLoadImgDao upImgDao = new UpLoadImgDaoImpl();
                
                Tea tea = teaDao.findTeaById(id);
                if (tea != null) {
                    File contextPath = new File(request.getServletContext().getRealPath("/"));
                    
                    // 删除商品记录
                    if (teaDao.teaDel(id)) {
                        // 删除图片记录和文件
                        if (upImgDao.imgDel(tea.getImgId())) {
                            File imgFile = new File(contextPath, tea.getUpLoadImg().getImgSrc());
                            if (imgFile.exists()) {
                                imgFile.delete();
                            }
                        }
                        request.setAttribute("teaMessage", "商品删除成功");
                    } else {
                        request.setAttribute("teaMessage", "商品删除失败");
                    }
                } else {
                    request.setAttribute("teaMessage", "商品不存在");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("teaMessage", "无效的商品ID");
            }
        }
        teaList(request, response);
    }

    // 搜索商品
    private void searchTea(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        int curPage = 1;
        String page = request.getParameter("page");
        if (page != null && !page.trim().isEmpty()) {
            try {
                curPage = Integer.parseInt(page);
            } catch (NumberFormatException e) {
                // 使用默认值1
            }
        }
        
        int maxSize = Integer.parseInt(request.getServletContext().getInitParameter("maxPageSize"));
        String keyword = request.getParameter("keyword");
        TeaDao teaDao = new TeaDaoImpl();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            PageBean pb = new PageBean(curPage, maxSize, teaDao.teaSearchCount(keyword));
            request.setAttribute("teaList", teaDao.searchTea(keyword, pb));
            request.setAttribute("pageBean", pb);
            request.setAttribute("keyword", keyword);
        } else {
            PageBean pb = new PageBean(curPage, maxSize, teaDao.teaReadCount());
            request.setAttribute("teaList", teaDao.teaList(pb));
            request.setAttribute("pageBean", pb);
        }
        
        request.getRequestDispatcher(TEALIST_PATH).forward(request, response);
    }

    // AJAX检查商品名是否存在
    private void teaFind(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String teaName = request.getParameter("param");
        TeaDao teaDao = new TeaDaoImpl();
        JSONObject json = new JSONObject();
        
        if (teaName != null && !teaName.trim().isEmpty()) {
            if (teaDao.findTeaByName(teaName.trim())) {
                json.put("info", "该商品名称已存在");
                json.put("status", "n");
            } else {
                json.put("info", "商品名称可用");
                json.put("status", "y");
            }
        } else {
            json.put("info", "商品名称不能为空");
            json.put("status", "n");
        }
        
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json.toString());
    }
}