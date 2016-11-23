package models.core

class AppUserBuilder {
  var loginId: String = "loginid"
  var loginPassword: String = "loginpass"
  var email: String = "yamada@_"
  var name: String = "山田"

  def build(): AppUser = {
    new AppUser(loginId, loginPassword, email, name)
  }

  def withLoginId(loginId: String): AppUserBuilder = {
    this.loginId = loginId
    this
  }

  def withLoginName(name: String): AppUserBuilder = {
    this.name = name
    this
  }
}
