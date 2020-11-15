package PwOk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import CommonService.CommonService;
import CommonService.CommonServiceImpl;
import DataBase.DataBaseService;
import DataBase.DataBaseServiceImpl;
import DataBase.Member;
import Login.LoginServiceImpl;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class PwOkServiceImpl implements PwOkService {

	CommonService comSrv = new CommonServiceImpl();
	PwOkServiceImpl pwSrv;
	Member member = new Member();
	DataBaseService dbSrv = new DataBaseServiceImpl();

	@Override // 아이디 중복확인
	public void checkId(Parent pwOkForm) {
		// TODO Auto-generated method stub
		TextField idTxt = (TextField) pwOkForm.lookup("#idTxt"); // 내가 입력한 아이디

		Button confirmIdBtn = (Button) pwOkForm.lookup("#confirmIdBtn");

		if (idTxt.getText().length() == 0) {
			comSrv.alertWindow("오류", "내용이 없습니다", AlertType.INFORMATION);
			return;
		}

		if (dbSrv.overlapCheck(idTxt.getText()) == false) {
			comSrv.alertWindow("없음", "아이디가 없습니다", AlertType.ERROR);
			return;
		}
		member = dbSrv.SearchMemberByID(idTxt.getText());
		comSrv.alertWindow("확인 완료", "아이디가 있습니다", AlertType.INFORMATION);
		confirmIdBtn.setDisable(!confirmIdBtn.isDisabled());
		idTxt.setDisable(!idTxt.isDisabled()); // 아이디입력 make disabled
		
	}

	@Override // 질문 중복확인
	public void checkQuiz(Parent pwOkForm) {
		// TODO Auto-generated method stub
		pwSrv = new PwOkServiceImpl();

		TextField answerTxt = (TextField) pwOkForm.lookup("#answerTxt");
		ComboBox<String> cmbQuiz = (ComboBox<String>) pwOkForm.lookup("#cmbQuiz");
		TextField newPwTxt = (TextField) pwOkForm.lookup("#newPwTxt");
		TextField newPwOkTxt = (TextField) pwOkForm.lookup("#newPwOkTxt");
		Button confirmPwBtn = (Button) pwOkForm.lookup("#confirmPwBtn");
		Button confirmQuizBtn = (Button) pwOkForm.lookup("#confirmQuizBtn");

		if (answerTxt.getText().length() == 0) {
			comSrv.alertWindow("오류", "내용이 없습니다", AlertType.INFORMATION);
			return;
		}

		if (!isComboBox(pwOkForm)) {
			return;
		}
		if (!isComboBox(pwOkForm)) {
			return;
		}
		if (answerTxt.getText().equals(member.getAnswer()) && cmbQuiz.getValue().equals(member.getQuiz())) {
			comSrv.alertWindow("확인 완료", "개인정보가 확인이 완료되었습니다", AlertType.INFORMATION);
//		pwSrv.setCheckBtn(pwOkForm);

			confirmQuizBtn.setDisable(!confirmQuizBtn.isDisabled());
			answerTxt.setDisable(!answerTxt.isDisabled());
			cmbQuiz.setDisable(!cmbQuiz.isDisabled());// 콤보박스 make disabled

			newPwTxt.setDisable(false);
			newPwOkTxt.setDisable(false);
			confirmPwBtn.setDisable(false); // 새로운비밀번호 make not disabled
		} else {
			comSrv.alertWindow("오류 발생", "개인정보가 일치하지않습니다", AlertType.ERROR);
			return;
		}
		confirmQuizBtn.setDisable(true);

	}

	@Override // 버튼 환경설정
	public void setCheckBtn(Parent pwOkForm) {
		// TODO Auto-generated method stub
		Button confirmIdBtn = (Button) pwOkForm.lookup("#confirmIdBtn");
		Button confirmQuizBtn = (Button) pwOkForm.lookup("#confirmQuizBtn");
		Button confirmPwBtn = (Button) pwOkForm.lookup("#confirmPwBtn");

		confirmQuizBtn.setOnMouseClicked(e -> {
			TextField newPwTxt = (TextField) pwOkForm.lookup("#newPwTxt");
			TextField newPwOkTxt = (TextField) pwOkForm.lookup("#newPwOkTxt");

			newPwTxt.setDisable(!newPwTxt.isDisable());
			newPwOkTxt.setDisable(!newPwOkTxt.isDisable());
			confirmPwBtn.setDisable(!confirmPwBtn.isDisable());
		});

	}

	@Override // 새로운 비밀번호 등록
	public void registerNewPw(Parent pwOkForm) {
		// TODO Auto-generated method stub
		String[] txtFldArr = { "#newPwTxt", "#newPwOkTxt" };
		Map<String, TextField> txtFldMap = comSrv.getTextFieldInfo(pwOkForm, txtFldArr);
		if (comSrv.isEmpty(txtFldMap, txtFldArr)) {
			comSrv.alertWindow("오류 발생", "작성을 완료해주세요", AlertType.ERROR);
			return;
		} else if (comparePw(pwOkForm)) {
//			System.out.println(comparePw(pwOkForm));
			comSrv.alertWindow("오류 발생", "비밀번호가 일치하지 않습니다", AlertType.ERROR);
			return;
		}
		System.out.println("다 입력했으니 DB에 등록하면 됨");
		TextField id = (TextField) (pwOkForm.lookup("#idTxt"));// 이건 로그인 서비스에서 받아와야 함

		String newPwOkTxt = txtFldMap.get(txtFldArr[1]).getText();

		String sql = ("update member set pw='" + newPwOkTxt + "' where id='" + id.getText() + "'");
		// update member set pwOk=? where id=?
		System.out.println(sql);
		if (dbSrv.excuteSql(sql) == false) {
			comSrv.alertWindow("에러", "알 수 없는 오류", AlertType.ERROR);
			return;
		} else {
			dbSrv.commit();
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setHeaderText("정보수정 완료");
			alert.setContentText("비밀번호가 완료되었습니다");

			alert.setOnCloseRequest(e -> {
				comSrv.WindowClose(pwOkForm);
			});
			alert.show();
		}

	}

	@Override // 비밀번호 중복확인
	public boolean comparePw(Parent pwOkform) {
		// TODO Auto-generated method stub
		TextField newPwTxt = (TextField) pwOkform.lookup("#newPwTxt");
		TextField newPwOkTxt = (TextField) pwOkform.lookup("#newPwOkTxt");

		System.out.println("oldPw를 DB에서 찾아와서 비교하는 곳");

		if (newPwTxt.getText().equals(newPwOkTxt.getText())) {
			return false;
		} else
			return true;
	}

	@Override // 콤보박스 내용 추가
	public void AddComboBox(Parent pwOkform) {
		// TODO Auto-generated method stub
		ComboBox<String> cmbQuiz = (ComboBox<String>) pwOkform.lookup("#cmbQuiz");

		List<String> items = new ArrayList<String>();
		items.add("어머니 성함은?");
		items.add("아버지 성함은?");
		items.add("보물 1호는?");//

		if (cmbQuiz != null) {
			for (String item : items) {
				cmbQuiz.getItems().add(item);
			}
		}
	}

	@Override
	public boolean isComboBox(Parent pwOkform) {
		// TODO Auto-generated method stub
		CommonService comSrv = new CommonServiceImpl();
		ComboBox<String> cmbQuiz = (ComboBox<String>) pwOkform.lookup("#cmbQuiz");
		if (cmbQuiz == null) {
			comSrv.alertWindow("오류 발생", "콤보박스가 비어 있습니다.", AlertType.ERROR);
			return false;
		} else if (cmbQuiz.getValue() == null) {
			comSrv.alertWindow("오류 발생", "콤보박스가 비어 있습니다.", AlertType.ERROR);
			return false;
		}
		return true;
	}

	@Override
	public String getComboBoxString(Parent pwOkform) {
		// TODO Auto-generated method stub
		ComboBox<String> cmbQuiz = (ComboBox<String>) pwOkform.lookup("#cmbQuiz");

		if (cmbQuiz == null) {
			return "";
		}
		return cmbQuiz.getValue().toString();
	}
}
