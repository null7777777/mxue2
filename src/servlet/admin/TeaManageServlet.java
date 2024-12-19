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
import model.UpLoadImg;
import dao.TeaDao;
import dao.CatalogDao;
import dao.UpLoadImgDao;
import dao.impl.TeaDaoImpl;
import dao.impl.CatalogDaoImpl;
import dao.impl.UpLoadImgDaoImpl;
import utils.RanUtil;

import net.sf.json.JSONObject;

@WebServlet("/jsp/admin/TeaManageServlet")
public class TeaManageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String TEALIST_PATH = "teaManage/teaList.jsp";
    private static final String TEAADD_PATH = "teaManage/teaAdd.jsp";
    private static final String TEAEDIT_PATH = "teaManage/teaEdit.jsp";
    private static final String TEADETAIL_PATH = "teaManage/teaDetail.jsp";
    private static final String TEAIMGDIR_PATH = "images/tea/teaimg/";
    
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

// 获取商品列表
    private void teaList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        TeaDao teaDao = new TeaDaoImpl();
        List<Tea> teaList = teaDao.teaList(); // 修改为符合接口的方法名
        request.setAttribute("teaList", teaList);
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
                Tea tea = teaDao.findTeaById(id); // 修改为符合接口的方法名
                if(tea != null) {
                    request.setAttribute("teaInfo", tea);
                    request.getRequestDispatcher(TEADETAIL_PATH).forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                // ID格式错误
            }
        }
        request.setAttribute("teaMessage", "商品不存在");
        teaList(request, response);
    }

    // 准备添加商品
    private void teaAddReq(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        CatalogDao catalogDao = new CatalogDaoImpl();
        List<Catalog> catalogs = catalogDao.getCatalog(); // 修改为符合接口的方法名
        request.setAttribute("catalog", catalogs);
        request.getRequestDispatcher(TEAADD_PATH).forward(request, response);
    }

// 添加商品
    private void teaAdd(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            if (!ServletFileUpload.isMultipartContent(request)) {
                request.setAttribute("teaMessage", "错误的表单类型");
                teaAddReq(request, response);
                return;
            }

            File contextPath = new File(request.getServletContext().getRealPath("/"));
            File dirPath = new File(contextPath, TEAIMGDIR_PATH);
            if (!dirPath.exists()) {
                dirPath.mkdirs();
            }

            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(MAX_FILE_SIZE);
            
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(MAX_FILE_SIZE);
            upload.setHeaderEncoding("UTF-8");

            List<FileItem> items = upload.parseRequest(request);
            Tea tea = new Tea();
            UpLoadImg upImg = new UpLoadImg();
            boolean hasImage = false;

            for (FileItem item : items) {
                if (item.isFormField()) {
                    processFormField(item, tea);
                } else {
                    if (item.getSize() > 0) {
                        hasImage = processUploadedFile(item, upImg, dirPath, contextPath);
                    }
                }
            }

            if (tea.getTeaName() == null || tea.getPrice() == null || !hasImage) {
                request.setAttribute("teaMessage", "请填写完整的商品信息");
                teaAddReq(request, response);
                return;
            }

            UpLoadImgDao upImgDao = new UpLoadImgDaoImpl();
            if (upImgDao.imgAdd(upImg)) { // 修改为符合接口的方法名
                tea.setImgId(upImg.getImgId());
                tea.setAddTime(new Date());

                TeaDao teaDao = new TeaDaoImpl();
                if (teaDao.addTea(tea)) { // 修改为符合接口的方法名
                    request.setAttribute("teaMessage", "商品添加成功");
                    teaList(request, response);
                    return;
                }
            }

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
        }
    }

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
                
                Tea tea = teaDao.findTeaById(teaId); // 修改为符合接口的方法名
                List<Catalog> catalogs = catalogDao.getCatalog(); // 修改为符合接口的方法名
                
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
        
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            request.setAttribute("teaMessage", "商品ID不能为空");
            teaList(request, response);
            return;
        }

        try {
            tea.setTeaId(Integer.parseInt(idStr));
            
            String teaName = request.getParameter("teaName");
            if (teaName != null && !teaName.trim().isEmpty()) {
                tea.setTeaName(teaName.trim());
            }
            
            String priceStr = request.getParameter("price");
            if (priceStr != null && !priceStr.trim().isEmpty()) {
                tea.setPrice(Double.parseDouble(priceStr));
            }
            
            String catalogIdStr = request.getParameter("catalog");
            if (catalogIdStr != null && !catalogIdStr.trim().isEmpty()) {
                tea.setCatalogId(Integer.parseInt(catalogIdStr));
            }
            
            String description = request.getParameter("description");
            if (description != null) {
                tea.setDescription(description.trim());
            }
            
            // 获取原有商品信息中的imgId和addTime
            TeaDao teaDao = new TeaDaoImpl();
            Tea oldTea = teaDao.findTeaById(tea.getTeaId());
            if (oldTea != null) {
                tea.setImgId(oldTea.getImgId());
                tea.setAddTime(oldTea.getAddTime());
            }
            
            if (teaDao.updateTea(tea)) { // 修改为符合接口的方法名
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
            
            Tea tea = teaDao.findTeaById(teaId);
            if (tea == null) {
                request.setAttribute("teaMessage", "商品不存在");
                teaList(request, response);
                return;
            }

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
                    UpLoadImg upImg = new UpLoadImg();
                    upImg.setImgId(tea.getImgId());
                    if (processUploadedFile(item, upImg, dirPath, contextPath)) {
                        if (upImgDao.imgUpdate(upImg)) { // 修改为符合接口的方法名
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
                    if (teaDao.deleteTea(id)) { // 修改为符合接口的方法名
                        // 删除图片记录和文件
                        if (upImgDao.imgDel(tea.getImgId())) { // 修改为符合接口的方法名
                            // 获取图片信息并删除文件
                            UpLoadImg img = upImgDao.findImgById(tea.getImgId());
                            if (img != null) {
                                File imgFile = new File(contextPath, img.getImgSrc());
                                if (imgFile.exists()) {
                                    imgFile.delete();
                                }
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
        String keyword = request.getParameter("keyword");
        TeaDao teaDao = new TeaDaoImpl();
        List<Tea> teaList;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 由于TeaDao接口中没有专门的搜索方法，我们获取所有商品后在内存中过滤
            teaList = teaDao.teaList().stream()
                .filter(tea -> tea.getTeaName().toLowerCase().contains(keyword.toLowerCase().trim()))
                .collect(java.util.stream.Collectors.toList());
            request.setAttribute("keyword", keyword);
        } else {
            teaList = teaDao.teaList();
        }
        
        request.setAttribute("teaList", teaList);
        request.getRequestDispatcher(TEALIST_PATH).forward(request, response);
    }

    // AJAX检查商品名是否存在
    private void teaFind(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String teaName = request.getParameter("param");
        TeaDao teaDao = new TeaDaoImpl();
        JSONObject json = new JSONObject();
        
        if (teaName != null && !teaName.trim().isEmpty()) {
            // 在所有商品中查找是否存在相同名称
            boolean exists = teaDao.teaList().stream()
                .anyMatch(tea -> tea.getTeaName().equalsIgnoreCase(teaName.trim()));
            
            if (exists) {
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
