package com.idega.block.book.presentation;

import java.rmi.RemoteException;

import javax.ejb.FinderException;

import com.idega.block.book.business.BookBusiness;
import com.idega.block.book.data.Book;
import com.idega.block.media.presentation.ImageInserter;
import com.idega.data.IDOException;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CloseButton;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SelectionBox;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextArea;
import com.idega.presentation.ui.TextInput;

public class BookEditor extends IWAdminWindow{

private int _bookID = -1;
private boolean _update = false;
private boolean _save = false;
private int _objectID = -1;
private Book _book;

private final static String IW_BUNDLE_IDENTIFIER="com.idega.block.book";
private IWBundle _iwb;
private IWResourceBundle _iwrb;

public BookEditor(){
  setWidth(500);
  setHeight(550);
  setUnMerged();
}

  public void main(IWContext iwc) throws Exception {
    _iwb = getBundle(iwc);
    _iwrb = getResourceBundle(iwc);
    addTitle(_iwrb.getLocalizedString("add_book","Add book"));

    try {
      _bookID = Integer.parseInt(iwc.getParameter(BookBusiness.PARAMETER_BOOK_ID));
    }
    catch (NumberFormatException e) {
      _bookID = -1;
    }

    String mode = iwc.getParameter(BookBusiness.PARAMETER_MODE);

    if ( mode.equalsIgnoreCase(BookBusiness.PARAMETER_EDIT) ) {
      if ( _bookID != -1 ) {
	_update = true;
	_book = getBookBusiness().getBook(_bookID);
	if ( _book == null ) _update = false;
      }
      processForm();
    }
    else if ( mode.equalsIgnoreCase(BookBusiness.PARAMETER_NEW) ) {
      processForm();
    }
    else if ( mode.equalsIgnoreCase(BookBusiness.PARAMETER_DELETE) ) {
      deleteBook();
    }
    else if ( mode.equalsIgnoreCase(BookBusiness.PARAMETER_SAVE) ) {
      saveBook(iwc);
    }
  }

  private void processForm() throws IDOException,FinderException,RemoteException {
    TextInput bookName = new TextInput(BookBusiness.PARAMETER_NAME);
      bookName.setLength(24);
    TextArea bookDescription = new TextArea(BookBusiness.PARAMETER_DESCRIPTION,54,12);
    TextInput bookYear = new TextInput(BookBusiness.PARAMETER_YEAR);
      bookYear.setLength(4);
      bookYear.setMaxlength(4);
    DropdownMenu publisher = getBookBusiness().getPublisherMenu();
    SelectionBox authors = getBookBusiness().getAuthorMenu(_bookID);
    SelectionBox categories = getBookBusiness().getCategoryMenu(_book);
    ImageInserter imageInsert = new ImageInserter(BookBusiness.PARAMETER_IMAGE_ID);
      imageInsert.setMaxImageWidth(130);
      imageInsert.setHasUseBox(false);

    if ( _update ) {
      if ( _book.getName() != null ) {
	bookName.setContent(_book.getName());
      }
      if ( _book.getDescription() != null ) {
	bookDescription.setContent(_book.getDescription());
      }
      if ( _book.getYear() != 0 ) {
	bookYear.setContent(String.valueOf(_book.getYear()));
      }
      if ( _book.getPublisherID() != -1 ) {
	publisher.setSelectedElement(String.valueOf(_book.getPublisherID()));
      }
      if ( _book.getImage() != -1 ) {
	imageInsert.setImageId(_book.getImage());
      }
    }
    addLeft(_iwrb.getLocalizedString("book_name","Book name")+":",bookName,true);
    addLeft(_iwrb.getLocalizedString("book_description","Description")+":",bookDescription,true);
    addLeft(_iwrb.getLocalizedString("book_year","Publish year")+":"+Text.NON_BREAKING_SPACE,bookYear,false);
    addLeft(_iwrb.getLocalizedString("book_publisher","Publisher")+":"+Text.NON_BREAKING_SPACE,publisher,false);
    addLeft(_iwrb.getLocalizedString("book_authors","Authors")+":",authors,true);
    addLeft(_iwrb.getLocalizedString("book_categories","Categories")+":",categories,true);
    addRight(_iwrb.getLocalizedString("image","Image")+":",imageInsert,true,false);

    addHiddenInput(new HiddenInput(BookBusiness.PARAMETER_BOOK_ID,Integer.toString(_bookID)));

    addSubmitButton(new CloseButton(_iwrb.getLocalizedImageButton("close","CLOSE")));
    addSubmitButton(new SubmitButton(_iwrb.getLocalizedImageButton("save","SAVE"),BookBusiness.PARAMETER_MODE,BookBusiness.PARAMETER_SAVE));
  }

  private void saveBook(IWContext iwc) {
    String bookName = iwc.getParameter(BookBusiness.PARAMETER_NAME);
    String bookDescription = iwc.getParameter(BookBusiness.PARAMETER_DESCRIPTION);
    String bookYear = iwc.getParameter(BookBusiness.PARAMETER_YEAR);
    String bookImageID = iwc.getParameter(BookBusiness.PARAMETER_IMAGE_ID);
    String bookPublisher = iwc.getParameter(BookBusiness.PARAMETER_PUBLISHER_ID);
    String[] bookAuthors = iwc.getParameterValues(BookBusiness.PARAMETER_AUTHORS);
    String[] bookCategories = iwc.getParameterValues(BookBusiness.PARAMETER_CATEGORIES);

    getBookBusiness().saveBook(_bookID,bookName,bookDescription,bookYear,bookImageID,bookPublisher,bookAuthors,bookCategories);

    setParentToReload();
    close();
  }

  private void deleteBook() {
    getBookBusiness().deleteBook(_bookID);
    setParentToReload();
    close();
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

  private BookBusiness getBookBusiness(){
    return BookBusiness.getBookBusinessInstace();
  }
}

